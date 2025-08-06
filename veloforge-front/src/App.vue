<template>
  <div id="app">
    <nav>
      <div class="nav-container">
        <router-link to="/" class="nav-brand">
          🚴 VeloForge
        </router-link>
        <ul class="nav-links">
          <li><router-link to="/">Home</router-link></li>
          <li><router-link to="/cyclists">Cyclists</router-link></li>
          <li><router-link to="/events">Events</router-link></li>
        </ul>
      </div>
    </nav>
    
    <main>
      <router-view />
    </main>
    
    <NotificationContainer />
  </div>
</template>

<script setup lang="ts">
// Import API configuration
import '@/api'
import NotificationContainer from '@/components/NotificationContainer.vue'
</script>