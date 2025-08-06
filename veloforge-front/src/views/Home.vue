<template>
  <div class="container page">
    <div class="card">
      <div class="card-header text-center">
        <h1 class="card-title">🚴 VeloForge MVP</h1>
        <p class="card-subtitle">Virtual Ultra Cycling Competition Platform</p>
      </div>

      <div class="grid grid-2 mt-4">
        <div class="card">
          <h3 class="card-title">🏃‍♂️ Cyclists</h3>
          <p>Manage cyclists with unique capabilities like power output, endurance, and mental resilience.</p>
          <router-link to="/cyclists" class="btn btn-primary">
            View Cyclists
          </router-link>
        </div>

        <div class="card">
          <h3 class="card-title">🏁 Events</h3>
          <p>Create and manage cycling events with real-time position tracking and leaderboards.</p>
          <router-link to="/events" class="btn btn-primary">
            View Events
          </router-link>
        </div>
      </div>

      <div class="card mt-4">
        <h3 class="card-title">🎯 MVP Features</h3>
        <div class="grid grid-3">
          <div>
            <h4>✅ Physics Simulation</h4>
            <p>Realistic speed calculations based on power output, fatigue, and environmental factors.</p>
          </div>
          <div>
            <h4>✅ Real-time Tracking</h4>
            <p>Live position updates every 30 seconds with WebSocket connections.</p>
          </div>
          <div>
            <h4>✅ Leaderboards</h4>
            <p>Dynamic rankings showing current positions and race progress.</p>
          </div>
        </div>
      </div>

      <div class="card mt-4">
        <h3 class="card-title">🚀 Quick Start</h3>
        <ol>
          <li>View or create cyclists with different capability profiles</li>
          <li>Create a new event or use the sample "Paris-Chartres Classic"</li>
          <li>Add cyclists to the event as participants</li>
          <li>Start the event to begin the simulation</li>
          <li>Watch real-time position updates and leaderboard changes</li>
        </ol>
      </div>

      <div class="stats-grid mt-4" v-if="stats">
        <div class="card text-center">
          <h4>{{ stats.cyclists }}</h4>
          <p>Cyclists</p>
        </div>
        <div class="card text-center">
          <h4>{{ stats.events }}</h4>
          <p>Events</p>
        </div>
        <div class="card text-center">
          <h4>{{ stats.activeEvents }}</h4>
          <p>Active Events</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useCyclistsStore } from '@/stores/cyclists'
import { useEventsStore } from '@/stores/events'

const cyclistsStore = useCyclistsStore()
const eventsStore = useEventsStore()

const stats = ref<{
  cyclists: number
  events: number
  activeEvents: number
} | null>(null)

onMounted(async () => {
  // Load data for stats
  await Promise.all([
    cyclistsStore.fetchCyclists(),
    eventsStore.fetchEvents()
  ])

  stats.value = {
    cyclists: cyclistsStore.cyclists.length,
    events: eventsStore.events.length,
    activeEvents: eventsStore.events.filter(e => e.status === 'started').length
  }
})
</script>

<style scoped>
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 1rem;
}

.stats-grid .card h4 {
  font-size: 2rem;
  margin: 0;
  color: var(--primary-color);
}

.stats-grid .card p {
  margin: 0.5rem 0 0 0;
  color: var(--text-secondary);
}

ol {
  padding-left: 1.5rem;
}

ol li {
  margin-bottom: 0.5rem;
}

h4 {
  margin-top: 0;
  margin-bottom: 0.5rem;
}
</style>