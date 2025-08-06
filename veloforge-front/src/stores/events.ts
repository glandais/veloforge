import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import {
  EventsService,
  PositionsService,
  type Event,
  type CreateEventRequest,
  type AddParticipantRequest,
  type LeaderboardEntry,
  type Position,
} from '@/api'

export const useEventsStore = defineStore('events', () => {
  const events = ref<Event[]>([])
  const currentEvent = ref<Event | null>(null)
  const leaderboard = ref<LeaderboardEntry[]>([])
  const positions = ref<Position[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)

  const getEvents = computed(() => events.value)
  const getCurrentEvent = computed(() => currentEvent.value)
  const getLeaderboard = computed(() => leaderboard.value)
  const getPositions = computed(() => positions.value)
  const isLoading = computed(() => loading.value)
  const hasError = computed(() => error.value !== null)

  async function fetchEvents() {
    loading.value = true
    error.value = null
    try {
      events.value = await EventsService.listEvents()
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'Failed to fetch events'
      console.error('Error fetching events:', e)
    } finally {
      loading.value = false
    }
  }

  async function createEvent(request: CreateEventRequest): Promise<Event | null> {
    loading.value = true
    error.value = null
    try {
      const newEvent = await EventsService.createEvent(request)
      events.value.push(newEvent)
      return newEvent
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'Failed to create event'
      console.error('Error creating event:', e)
      return null
    } finally {
      loading.value = false
    }
  }

  async function getEvent(id: string): Promise<Event | null> {
    // Check if event is already in store
    const existing = events.value.find((e) => e.id === id)
    if (existing) {
      currentEvent.value = existing
      return existing
    }

    loading.value = true
    error.value = null
    try {
      const event = await EventsService.getEvent(id)
      currentEvent.value = event
      // Add to store if not already present
      if (!events.value.find((e) => e.id === event.id)) {
        events.value.push(event)
      }
      return event
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'Failed to fetch event'
      console.error('Error fetching event:', e)
      return null
    } finally {
      loading.value = false
    }
  }

  async function startEvent(id: string): Promise<Event | null> {
    loading.value = true
    error.value = null
    try {
      const event = await EventsService.startEvent(id)

      // Update in store
      const index = events.value.findIndex((e) => e.id === id)
      if (index !== -1) {
        events.value[index] = event
      }

      if (currentEvent.value?.id === id) {
        currentEvent.value = event
      }

      return event
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'Failed to start event'
      console.error('Error starting event:', e)
      return null
    } finally {
      loading.value = false
    }
  }

  async function addParticipant(eventId: string, request: AddParticipantRequest): Promise<boolean> {
    loading.value = true
    error.value = null
    try {
      await EventsService.addParticipant(eventId, request)

      // Refresh the event to get updated participants
      await getEvent(eventId)
      return true
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'Failed to add participant'
      console.error('Error adding participant:', e)
      return false
    } finally {
      loading.value = false
    }
  }

  async function fetchLeaderboard(eventId: string) {
    try {
      leaderboard.value = await EventsService.getLeaderboard(eventId)
    } catch (e) {
      console.error('Error fetching leaderboard:', e)
      // Don't set error for leaderboard as it's not critical
    }
  }

  async function fetchPositions(eventId: string) {
    try {
      positions.value = await PositionsService.getCurrentPositions(eventId)
    } catch (e) {
      console.error('Error fetching positions:', e)
      // Don't set error for positions as it's not critical
    }
  }

  function clearError() {
    error.value = null
  }

  function clearCurrentEvent() {
    currentEvent.value = null
    leaderboard.value = []
    positions.value = []
  }

  return {
    events: getEvents,
    currentEvent: getCurrentEvent,
    leaderboard: getLeaderboard,
    positions: getPositions,
    loading: isLoading,
    error: computed(() => error.value),
    fetchEvents,
    createEvent,
    getEvent,
    startEvent,
    addParticipant,
    fetchLeaderboard,
    fetchPositions,
    clearError,
    clearCurrentEvent,
  }
})
