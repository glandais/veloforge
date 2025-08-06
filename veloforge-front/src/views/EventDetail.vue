<template>
  <div class="container page">
    <!-- Loading State -->
    <div v-if="eventsStore.loading && !eventsStore.currentEvent" class="loading">
      <div class="spinner"></div>
      Loading event...
    </div>

    <!-- Error State -->
    <div v-else-if="eventsStore.error" class="card" style="border-color: var(--danger-color)">
      <p style="color: var(--danger-color)">{{ eventsStore.error }}</p>
      <button @click="loadEvent" class="btn btn-primary">Retry</button>
    </div>

    <!-- Event Details -->
    <div v-else-if="event" class="event-detail">
      <!-- Header -->
      <div class="event-header">
        <div>
          <h1>{{ event.name }}</h1>
          <div class="flex items-center gap-2 mt-2">
            <span
              class="badge"
              :class="{
                'badge-info': event.status === 'planned',
                'badge-success': event.status === 'started',
                'badge-warning': event.status === 'finished',
              }"
            >
              {{ event.status }}
            </span>
            <span class="badge badge-info">{{ event.type }}</span>
          </div>
        </div>

        <div class="event-actions">
          <button
            v-if="event.status === 'planned' && hasParticipants"
            @click="startEvent"
            class="btn btn-success"
            :disabled="eventsStore.loading"
          >
            🚀 Start Event
          </button>

          <button @click="showAddParticipant = true" class="btn btn-primary">
            👥 Add Participant
          </button>
        </div>
      </div>

      <!-- Route Information -->
      <div class="card">
        <h3 class="card-title">📍 Route Information</h3>
        <div class="route-info">
          <div class="info-row">
            <span>Name:</span>
            <span>{{ event.route.name }}</span>
          </div>
          <div class="info-row">
            <span>Distance:</span>
            <span>{{ event.route.distance }}km</span>
          </div>
          <div class="info-row">
            <span>Start:</span>
            <span
              >{{ event.route.startPoint.latitude }}°, {{ event.route.startPoint.longitude }}°</span
            >
          </div>
          <div class="info-row">
            <span>End:</span>
            <span>{{ event.route.endPoint.latitude }}°, {{ event.route.endPoint.longitude }}°</span>
          </div>
        </div>
      </div>

      <!-- Map Component -->
      <div v-if="event.status === 'started'" class="card">
        <h3 class="card-title">🗺️ Live Race Map</h3>
        <EventMap :route="event.route" :positions="positions" :cyclists="cyclistsStore.cyclists" />
      </div>

      <!-- Live Tracking (only show during active event) -->
      <div v-if="event.status === 'started'" class="grid grid-2">
        <!-- Leaderboard -->
        <div class="card">
          <div class="card-header">
            <h3 class="card-title">🏆 Leaderboard</h3>
            <div class="flex items-center gap-2">
              <div
                class="connection-status"
                :class="{ connected: wsConnected, connecting: wsConnecting }"
              >
                {{ wsConnecting ? 'Connecting...' : wsConnected ? 'Live' : 'Disconnected' }}
              </div>
              <button
                @click="refreshData"
                class="btn btn-outline"
                style="padding: 0.25rem 0.5rem; font-size: 0.75rem"
              >
                🔄
              </button>
            </div>
          </div>

          <div v-if="leaderboard.length > 0" class="leaderboard">
            <div v-for="entry in leaderboard" :key="entry.cyclistId" class="leaderboard-entry">
              <div class="position">{{ entry.position }}</div>
              <div class="cyclist-info">
                <div class="name">{{ entry.cyclistName }}</div>
                <div class="stats">
                  {{ Math.round(Number(entry.distance)) }}km |
                  {{ Math.round(Number(entry.averageSpeed)) }} km/h
                </div>
              </div>
              <div class="status">
                <span
                  class="badge"
                  :class="{
                    'badge-success': entry.status === 'riding',
                    'badge-warning': entry.status === 'resting',
                    'badge-info': entry.status === 'finished',
                  }"
                >
                  {{ entry.status }}
                </span>
              </div>
            </div>
          </div>
          <div v-else class="text-center" style="padding: 2rem; color: var(--text-secondary)">
            No position data available
          </div>
        </div>

        <!-- Map (Placeholder) -->
        <div class="card">
          <h3 class="card-title">🗺️ Live Positions</h3>
          <div class="map-placeholder">
            <div class="route-visualization">
              <div class="route-line"></div>
              <div
                v-for="(position, index) in positions"
                :key="position.cyclistId"
                class="cyclist-marker"
                :style="{
                  left: (position.distance / event.route.distance) * 100 + '%',
                  backgroundColor: getMarkerColor(index),
                }"
                :title="`${getCyclistName(position.cyclistId)}: ${Math.round(position.distance)}km`"
              >
                🚴
              </div>
            </div>
            <div class="route-labels">
              <span>Start</span>
              <span>{{ event.route.distance }}km Finish</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Participants -->
      <div class="card">
        <h3 class="card-title">👥 Participants ({{ participants.length }})</h3>

        <div v-if="participants.length > 0" class="participants-list">
          <div
            v-for="participant in participants"
            :key="participant.cyclistId"
            class="participant-row"
          >
            <div class="participant-info">
              <span class="cyclist-name">{{ getCyclistName(participant.cyclistId) }}</span>
              <span class="cyclist-id">ID: {{ participant.cyclistId }}</span>
            </div>
            <div class="participant-status">
              <span
                class="badge"
                :class="{
                  'badge-info': participant.status === 'registered',
                  'badge-success':
                    participant.status === 'started' ||
                    participant.status === 'riding' ||
                    participant.status === 'finished',
                  'badge-warning': participant.status === 'resting',
                  'badge-danger': participant.status === 'dnf',
                }"
              >
                {{ participant.status }}
              </span>
            </div>
          </div>
        </div>
        <div v-else class="text-center" style="padding: 2rem; color: var(--text-secondary)">
          No participants yet. Add cyclists to this event!
        </div>
      </div>
    </div>

    <!-- Add Participant Modal -->
    <div v-if="showAddParticipant" class="modal-overlay" @click="showAddParticipant = false">
      <div class="modal" @click.stop>
        <div class="modal-header">
          <h2>Add Participant</h2>
          <button @click="showAddParticipant = false" class="btn btn-outline">×</button>
        </div>

        <div class="modal-body">
          <div v-if="availableCyclists.length > 0">
            <label class="form-label">Select Cyclist</label>
            <select v-model="selectedCyclistId" class="form-input">
              <option value="">Choose a cyclist...</option>
              <option v-for="cyclist in availableCyclists" :key="cyclist.id" :value="cyclist.id">
                {{ cyclist.name }} ({{ cyclist.capabilities.pma }}W)
              </option>
            </select>
          </div>
          <div v-else class="text-center">
            <p>
              No available cyclists.
              <router-link to="/cyclists" class="btn btn-outline"
                >Create cyclists first</router-link
              >
            </p>
          </div>

          <div class="modal-footer">
            <button @click="showAddParticipant = false" class="btn btn-secondary">Cancel</button>
            <button
              @click="addParticipant"
              class="btn btn-primary"
              :disabled="!selectedCyclistId || eventsStore.loading"
            >
              {{ eventsStore.loading ? 'Adding...' : 'Add Participant' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import { useEventsStore } from '@/stores/events'
import { useCyclistsStore } from '@/stores/cyclists'
import { useWebSocket } from '@/composables/useWebSocket'
import EventMap from '@/components/EventMap.vue'

const route = useRoute()
const eventsStore = useEventsStore()
const cyclistsStore = useCyclistsStore()

const eventId = route.params.id as string
const showAddParticipant = ref(false)
const selectedCyclistId = ref('')

// WebSocket connection for live updates
const {
  connected: wsConnected,
  connecting: wsConnecting,
  positions: wsPositions,
  connect: connectWS,
} = useWebSocket(eventId)

const event = computed(() => eventsStore.currentEvent)
const leaderboard = computed(() => eventsStore.leaderboard)
const positions = computed(() =>
  wsPositions.value.length > 0 ? wsPositions.value : eventsStore.positions,
)
const participants = computed(() => event.value?.participants || [])
const hasParticipants = computed(() => participants.value.length > 0)

const availableCyclists = computed(() => {
  const participantIds = new Set(participants.value.map((p) => p.cyclistId))
  return cyclistsStore.cyclists.filter((c) => !participantIds.has(c.id!))
})

async function loadEvent() {
  await eventsStore.getEvent(eventId)
  await cyclistsStore.fetchCyclists()

  if (event.value?.status === 'started') {
    // Load initial data
    await Promise.all([eventsStore.fetchLeaderboard(eventId), eventsStore.fetchPositions(eventId)])

    // Start WebSocket connection
    connectWS()
  }
}

async function startEvent() {
  const result = await eventsStore.startEvent(eventId)
  if (result) {
    // Start real-time updates
    await loadEvent()
  }
}

async function addParticipant() {
  if (!selectedCyclistId.value) return

  const success = await eventsStore.addParticipant(eventId, {
    cyclistId: selectedCyclistId.value,
  })

  if (success) {
    showAddParticipant.value = false
    selectedCyclistId.value = ''
  }
}

function getCyclistName(cyclistId: string): string {
  const cyclist = cyclistsStore.cyclists.find((c) => c.id === cyclistId)
  return cyclist?.name || `Cyclist ${cyclistId.slice(-6)}`
}

function getMarkerColor(index: number): string {
  const colors = ['#ef4444', '#3b82f6', '#10b981', '#f59e0b', '#8b5cf6']
  return colors[index % colors.length]
}

async function refreshData() {
  if (event.value?.status === 'started') {
    await Promise.all([eventsStore.fetchLeaderboard(eventId), eventsStore.fetchPositions(eventId)])
  }
}

// Periodic refresh for non-WebSocket data
let refreshInterval: number | null = null

onMounted(async () => {
  await loadEvent()

  // Set up periodic refresh
  if (event.value?.status === 'started') {
    refreshInterval = window.setInterval(refreshData, 10000) // Every 10 seconds
  }
})

onUnmounted(() => {
  if (refreshInterval) {
    clearInterval(refreshInterval)
  }
  eventsStore.clearCurrentEvent()
})
</script>

<style scoped>
.event-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 2rem;
  gap: 1rem;
}

.event-actions {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.route-info {
  display: grid;
  gap: 0.5rem;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.25rem 0;
  font-size: 0.875rem;
}

.info-row span:first-child {
  color: var(--text-secondary);
  font-weight: 500;
}

.connection-status {
  padding: 0.25rem 0.5rem;
  border-radius: 0.25rem;
  font-size: 0.75rem;
  font-weight: 500;
  text-transform: uppercase;
}

.connection-status.connected {
  background: rgb(16 185 129 / 0.1);
  color: var(--success-color);
}

.connection-status.connecting {
  background: rgb(245 158 11 / 0.1);
  color: var(--warning-color);
}

.leaderboard {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.leaderboard-entry {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 0.75rem;
  background: var(--background-color);
  border-radius: 0.375rem;
  border: 1px solid var(--border-color);
}

.position {
  font-size: 1.25rem;
  font-weight: bold;
  color: var(--primary-color);
  min-width: 2rem;
  text-align: center;
}

.cyclist-info {
  flex: 1;
}

.name {
  font-weight: 500;
  color: var(--text-primary);
}

.stats {
  font-size: 0.875rem;
  color: var(--text-secondary);
}

.map-placeholder {
  padding: 2rem;
  background: var(--background-color);
  border-radius: 0.375rem;
  border: 1px solid var(--border-color);
}

.route-visualization {
  position: relative;
  height: 60px;
  margin-bottom: 1rem;
}

.route-line {
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, var(--success-color), var(--primary-color));
  border-radius: 2px;
  transform: translateY(-50%);
}

.cyclist-marker {
  position: absolute;
  top: 50%;
  transform: translate(-50%, -50%);
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 16px;
  border: 2px solid white;
  box-shadow: var(--shadow);
  cursor: pointer;
}

.route-labels {
  display: flex;
  justify-content: space-between;
  font-size: 0.875rem;
  color: var(--text-secondary);
  font-weight: 500;
}

.participants-list {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.participant-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem;
  background: var(--background-color);
  border-radius: 0.375rem;
  border: 1px solid var(--border-color);
}

.participant-info {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.cyclist-name {
  font-weight: 500;
  color: var(--text-primary);
}

.cyclist-id {
  font-size: 0.75rem;
  color: var(--text-secondary);
}

/* Modal styles */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal {
  background: var(--surface-color);
  border-radius: 0.5rem;
  max-width: 400px;
  width: 90%;
  box-shadow: var(--shadow-lg);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem;
  border-bottom: 1px solid var(--border-color);
}

.modal-header h2 {
  margin: 0;
}

.modal-body {
  padding: 1.5rem;
}

.modal-footer {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
  margin-top: 1.5rem;
}

@media (max-width: 768px) {
  .event-header {
    flex-direction: column;
    align-items: stretch;
  }

  .event-actions {
    justify-content: flex-end;
  }

  .grid-2 {
    grid-template-columns: 1fr;
  }
}
</style>
