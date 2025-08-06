import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { CyclistsService, type Cyclist, type CreateCyclistRequest } from '@/api'

export const useCyclistsStore = defineStore('cyclists', () => {
  const cyclists = ref<Cyclist[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)

  const getCyclists = computed(() => cyclists.value)
  const isLoading = computed(() => loading.value)

  async function fetchCyclists() {
    loading.value = true
    error.value = null
    try {
      cyclists.value = await CyclistsService.listCyclists()
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'Failed to fetch cyclists'
      console.error('Error fetching cyclists:', e)
    } finally {
      loading.value = false
    }
  }

  async function createCyclist(request: CreateCyclistRequest): Promise<Cyclist | null> {
    loading.value = true
    error.value = null
    try {
      const newCyclist = await CyclistsService.createCyclist(request)
      cyclists.value.push(newCyclist)
      return newCyclist
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'Failed to create cyclist'
      console.error('Error creating cyclist:', e)
      return null
    } finally {
      loading.value = false
    }
  }

  async function getCyclist(id: string): Promise<Cyclist | null> {
    // Check if cyclist is already in store
    const existing = cyclists.value.find((c) => c.id === id)
    if (existing) return existing

    loading.value = true
    error.value = null
    try {
      const cyclist = await CyclistsService.getCyclist(id)
      // Add to store if not already present
      if (!cyclists.value.find((c) => c.id === cyclist.id)) {
        cyclists.value.push(cyclist)
      }
      return cyclist
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'Failed to fetch cyclist'
      console.error('Error fetching cyclist:', e)
      return null
    } finally {
      loading.value = false
    }
  }

  function clearError() {
    error.value = null
  }

  return {
    cyclists: getCyclists,
    loading: isLoading,
    error: computed(() => error.value),
    fetchCyclists,
    createCyclist,
    getCyclist,
    clearError,
  }
})
