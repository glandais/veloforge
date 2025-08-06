<template>
  <div class="container page">
    <div class="flex justify-between items-center mb-4">
      <h1>🏁 Events</h1>
      <button @click="showCreateForm = true" class="btn btn-primary">Create Event</button>
    </div>

    <!-- Loading State -->
    <div v-if="eventsStore.loading" class="loading">
      <div class="spinner"></div>
      Loading events...
    </div>

    <!-- Error State -->
    <div v-else-if="eventsStore.error" class="card" style="border-color: var(--danger-color)">
      <p style="color: var(--danger-color)">{{ eventsStore.error }}</p>
      <button @click="loadEvents" class="btn btn-primary">Retry</button>
    </div>

    <!-- Events Grid -->
    <div v-else class="grid grid-2">
      <div v-for="event in eventsStore.events" :key="event.id" class="card">
        <div class="card-header">
          <h3 class="card-title">{{ event.name }}</h3>
          <div class="flex items-center gap-2">
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

        <div class="event-details">
          <div class="detail-row">
            <span>📍 Route:</span>
            <span>{{ event.route.name }}</span>
          </div>
          <div class="detail-row">
            <span>📏 Distance:</span>
            <span>{{ event.route.distance }}km</span>
          </div>
          <div class="detail-row">
            <span>👥 Participants:</span>
            <span>{{ event.participants?.length || 0 }}</span>
          </div>
          <div v-if="event.startTime" class="detail-row">
            <span>⏰ Started:</span>
            <span>{{ formatDate(event.startTime) }}</span>
          </div>
        </div>

        <div class="event-actions mt-4">
          <router-link :to="`/events/${event.id}`" class="btn btn-primary">
            View Details
          </router-link>

          <button
            v-if="event.status === 'planned' && event.participants && event.participants.length > 0"
            @click="startEvent(event.id!)"
            class="btn btn-success"
            :disabled="eventsStore.loading"
          >
            Start Event
          </button>
        </div>
      </div>

      <!-- Empty State -->
      <div v-if="eventsStore.events.length === 0" class="card">
        <div class="text-center">
          <h3>No Events Found</h3>
          <p>Create your first event to get started!</p>
          <button @click="showCreateForm = true" class="btn btn-primary">Create Event</button>
        </div>
      </div>
    </div>

    <!-- Create Event Modal -->
    <div v-if="showCreateForm" class="modal-overlay" @click="showCreateForm = false">
      <div class="modal" @click.stop>
        <div class="modal-header">
          <h2>Create New Event</h2>
          <button @click="showCreateForm = false" class="btn btn-outline">×</button>
        </div>

        <form @submit.prevent="createEvent" class="modal-body">
          <div class="form-group">
            <label class="form-label">Event Name</label>
            <input
              v-model="newEvent.name"
              type="text"
              class="form-input"
              required
              maxlength="100"
              placeholder="Enter event name"
            />
          </div>

          <div class="form-group">
            <label class="form-label">Event Type</label>
            <select v-model="newEvent.type" class="form-input" required>
              <option value="point-to-point">Point to Point</option>
              <option value="loop">Loop</option>
              <option value="brevet">Brevet</option>
            </select>
          </div>

          <h4>Route Information</h4>

          <div class="form-group">
            <label class="form-label">Route Name</label>
            <input
              v-model="newEvent.route.name"
              type="text"
              class="form-input"
              required
              placeholder="Enter route name"
            />
          </div>

          <div class="form-group">
            <label class="form-label">Distance (km)</label>
            <input
              v-model.number="newEvent.route.distance"
              type="number"
              class="form-input"
              min="1"
              max="1000"
              required
            />
          </div>

          <div class="grid grid-2">
            <div>
              <h5>Start Point</h5>
              <div class="form-group">
                <label class="form-label">Latitude</label>
                <input
                  v-model.number="newEvent.route.startPoint.latitude"
                  type="number"
                  class="form-input"
                  step="any"
                  min="-90"
                  max="90"
                  required
                />
              </div>
              <div class="form-group">
                <label class="form-label">Longitude</label>
                <input
                  v-model.number="newEvent.route.startPoint.longitude"
                  type="number"
                  class="form-input"
                  step="any"
                  min="-180"
                  max="180"
                  required
                />
              </div>
            </div>

            <div>
              <h5>End Point</h5>
              <div class="form-group">
                <label class="form-label">Latitude</label>
                <input
                  v-model.number="newEvent.route.endPoint.latitude"
                  type="number"
                  class="form-input"
                  step="any"
                  min="-90"
                  max="90"
                  required
                />
              </div>
              <div class="form-group">
                <label class="form-label">Longitude</label>
                <input
                  v-model.number="newEvent.route.endPoint.longitude"
                  type="number"
                  class="form-input"
                  step="any"
                  min="-180"
                  max="180"
                  required
                />
              </div>
            </div>
          </div>

          <div class="modal-footer">
            <button type="button" @click="showCreateForm = false" class="btn btn-secondary">
              Cancel
            </button>
            <button type="submit" class="btn btn-primary" :disabled="eventsStore.loading">
              {{ eventsStore.loading ? 'Creating...' : 'Create Event' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useEventsStore } from '@/stores/events'
import { CreateEventRequest } from '@/api'

const eventsStore = useEventsStore()

const showCreateForm = ref(false)
const newEvent = ref<CreateEventRequest>({
  name: '',
  type: CreateEventRequest.type.POINT_TO_POINT,
  route: {
    name: '',
    distance: 100,
    startPoint: {
      latitude: 48.8566, // Paris
      longitude: 2.3522,
    },
    endPoint: {
      latitude: 48.4469, // Chartres
      longitude: 1.4869,
    },
  },
})

async function loadEvents() {
  await eventsStore.fetchEvents()
}

async function createEvent() {
  const event = await eventsStore.createEvent(newEvent.value)
  if (event) {
    showCreateForm.value = false
    // Reset form
    newEvent.value = {
      name: '',
      type: CreateEventRequest.type.POINT_TO_POINT,
      route: {
        name: '',
        distance: 100,
        startPoint: {
          latitude: 48.8566,
          longitude: 2.3522,
        },
        endPoint: {
          latitude: 48.4469,
          longitude: 1.4869,
        },
      },
    }
  }
}

async function startEvent(eventId: string) {
  await eventsStore.startEvent(eventId)
}

function formatDate(dateString: string) {
  return new Date(dateString).toLocaleString()
}

onMounted(() => {
  loadEvents()
})
</script>

<style scoped>
.event-details {
  margin: 1rem 0;
}

.detail-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.25rem 0;
  font-size: 0.875rem;
}

.detail-row span:first-child {
  color: var(--text-secondary);
  font-weight: 500;
}

.event-actions {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.modal h5 {
  margin: 0 0 0.5rem 0;
  font-size: 0.875rem;
  font-weight: 600;
  color: var(--text-primary);
}

/* Modal styles inherited from Cyclists.vue */
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
  max-width: 600px;
  width: 90%;
  max-height: 90vh;
  overflow-y: auto;
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

select.form-input {
  cursor: pointer;
}
</style>
