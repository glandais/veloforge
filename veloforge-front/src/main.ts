import { createApp } from 'vue'
import { createPinia } from 'pinia'
import { createRouter, createWebHistory } from 'vue-router'
import App from './App.vue'
import './style.css'

// Import views
import Home from './views/Home.vue'
import Cyclists from './views/Cyclists.vue'
import Events from './views/Events.vue'
import EventDetail from './views/EventDetail.vue'

const routes = [
  { path: '/', name: 'Home', component: Home },
  { path: '/cyclists', name: 'Cyclists', component: Cyclists },
  { path: '/events', name: 'Events', component: Events },
  { path: '/events/:id', name: 'EventDetail', component: EventDetail, props: true },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

const pinia = createPinia()

createApp(App).use(pinia).use(router).mount('#app')
