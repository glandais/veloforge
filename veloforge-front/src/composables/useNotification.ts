import { ref } from 'vue'

export interface Notification {
  id: string
  type: 'success' | 'error' | 'warning' | 'info'
  title: string
  message?: string
  duration?: number
}

const notifications = ref<Notification[]>([])

export function useNotification() {
  function notify(notification: Omit<Notification, 'id'>) {
    const id = Date.now().toString()
    const newNotification: Notification = {
      id,
      duration: 5000,
      ...notification
    }
    
    notifications.value.push(newNotification)
    
    if (newNotification.duration && newNotification.duration > 0) {
      setTimeout(() => {
        dismiss(id)
      }, newNotification.duration)
    }
  }
  
  function dismiss(id: string) {
    const index = notifications.value.findIndex(n => n.id === id)
    if (index > -1) {
      notifications.value.splice(index, 1)
    }
  }
  
  function success(title: string, message?: string) {
    notify({ type: 'success', title, message })
  }
  
  function error(title: string, message?: string) {
    notify({ type: 'error', title, message })
  }
  
  function warning(title: string, message?: string) {
    notify({ type: 'warning', title, message })
  }
  
  function info(title: string, message?: string) {
    notify({ type: 'info', title, message })
  }
  
  return {
    notifications,
    notify,
    dismiss,
    success,
    error,
    warning,
    info
  }
}