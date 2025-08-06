import { ref, onUnmounted } from 'vue'
import type { Position } from '@/api'

export interface WebSocketMessage {
  type: string
  data: any
}

export function useWebSocket(eventId: string) {
  const connected = ref(false)
  const connecting = ref(false)
  const error = ref<string | null>(null)
  const positions = ref<Position[]>([])

  let socket: WebSocket | null = null
  let reconnectTimer: number | null = null
  let reconnectAttempts = 0
  const maxReconnectAttempts = 5

  function connect() {
    if (connecting.value || connected.value) return

    connecting.value = true
    error.value = null

    const wsUrl = `ws://localhost:8080/ws/events/${eventId}/positions`

    try {
      socket = new WebSocket(wsUrl)

      socket.onopen = () => {
        console.log('WebSocket connected for event:', eventId)
        connected.value = true
        connecting.value = false
        reconnectAttempts = 0
        error.value = null
      }

      socket.onmessage = (event) => {
        try {
          const message: WebSocketMessage = JSON.parse(event.data)

          if (message.type === 'positions' && Array.isArray(message.data)) {
            positions.value = message.data as Position[]
          }
        } catch (e) {
          console.error('Error parsing WebSocket message:', e)
        }
      }

      socket.onclose = (event) => {
        console.log('WebSocket disconnected:', event.code, event.reason)
        connected.value = false
        connecting.value = false

        // Attempt to reconnect if it wasn't a clean close
        if (event.code !== 1000 && reconnectAttempts < maxReconnectAttempts) {
          scheduleReconnect()
        }
      }

      socket.onerror = (event) => {
        console.error('WebSocket error:', event)
        error.value = 'WebSocket connection error'
        connecting.value = false
      }
    } catch (e) {
      console.error('Failed to create WebSocket:', e)
      error.value = 'Failed to create WebSocket connection'
      connecting.value = false
    }
  }

  function scheduleReconnect() {
    if (reconnectTimer) return

    reconnectAttempts++
    const delay = Math.min(1000 * Math.pow(2, reconnectAttempts), 30000) // Exponential backoff, max 30s

    console.log(`Scheduling WebSocket reconnect attempt ${reconnectAttempts} in ${delay}ms`)

    reconnectTimer = window.setTimeout(() => {
      reconnectTimer = null
      connect()
    }, delay)
  }

  function disconnect() {
    if (reconnectTimer) {
      clearTimeout(reconnectTimer)
      reconnectTimer = null
    }

    if (socket && socket.readyState === WebSocket.OPEN) {
      socket.close(1000, 'Manual disconnect')
    }

    socket = null
    connected.value = false
    connecting.value = false
    reconnectAttempts = 0
  }

  function send(message: any) {
    if (socket && socket.readyState === WebSocket.OPEN) {
      socket.send(JSON.stringify(message))
    } else {
      console.warn('WebSocket is not connected, cannot send message')
    }
  }

  // Auto-cleanup on component unmount
  onUnmounted(() => {
    disconnect()
  })

  return {
    connected: readonly(connected),
    connecting: readonly(connecting),
    error: readonly(error),
    positions: readonly(positions),
    connect,
    disconnect,
    send,
  }
}

// Helper to make refs readonly
function readonly<T>(ref: any) {
  return computed(() => ref.value)
}

import { computed } from 'vue'
