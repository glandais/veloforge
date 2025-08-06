<template>
  <div class="map-container">
    <div ref="mapElement" class="event-map"></div>
    <div v-if="positions.length > 0" class="map-legend">
      <h4>Cyclists</h4>
      <div v-for="position in positions" :key="position.cyclistId" class="legend-item">
        <span class="cyclist-marker" :style="`background-color: ${getCyclistColor(position.cyclistId)}`"></span>
        <span>{{ getCyclistName(position.cyclistId) }}</span>
        <span class="speed">{{ position.speed?.toFixed(1) }} km/h</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch } from 'vue'
import L from 'leaflet'
import type { Position } from '@/api'
import 'leaflet/dist/leaflet.css'

const props = defineProps<{
  route: {
    startPoint: { latitude: number; longitude: number }
    endPoint: { latitude: number; longitude: number }
    waypoints?: Array<{
      position: { latitude: number; longitude: number }
      distance: number
      elevation?: number
    }>
  }
  positions: Position[]
  cyclists?: Array<{ id: string; name: string }>
}>()

const mapElement = ref<HTMLElement>()
let map: L.Map | null = null
let routeLine: L.Polyline | null = null
let cyclistMarkers = new Map<string, L.Marker>()

const colors = ['#FF6B6B', '#4ECDC4', '#45B7D1', '#FFA07A', '#98D8C8', '#F7DC6F', '#BB8FCE']

const getCyclistColor = (cyclistId: string) => {
  const index = props.positions.findIndex(p => p.cyclistId === cyclistId)
  return colors[index % colors.length]
}

const getCyclistName = (cyclistId: string) => {
  const cyclist = props.cyclists?.find(c => c.id === cyclistId)
  return cyclist?.name || `Cyclist ${cyclistId.slice(0, 8)}`
}

const initMap = () => {
  if (!mapElement.value) return

  // Initialize map centered on route start
  map = L.map(mapElement.value).setView(
    [props.route.startPoint.latitude, props.route.startPoint.longitude],
    10
  )

  // Add tile layer
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '© OpenStreetMap contributors'
  }).addTo(map)

  // Draw route
  drawRoute()

  // Add start/finish markers
  L.marker([props.route.startPoint.latitude, props.route.startPoint.longitude])
    .addTo(map)
    .bindPopup('Start')

  L.marker([props.route.endPoint.latitude, props.route.endPoint.longitude])
    .addTo(map)
    .bindPopup('Finish')
}

const drawRoute = () => {
  if (!map) return

  const routePoints: L.LatLngExpression[] = []
  
  if (props.route.waypoints && props.route.waypoints.length > 0) {
    // Use waypoints if available
    props.route.waypoints.forEach(wp => {
      routePoints.push([wp.position.latitude, wp.position.longitude])
    })
  } else {
    // Simple line from start to finish
    routePoints.push([props.route.startPoint.latitude, props.route.startPoint.longitude])
    routePoints.push([props.route.endPoint.latitude, props.route.endPoint.longitude])
  }

  // Remove old route line if exists
  if (routeLine) {
    map.removeLayer(routeLine)
  }

  // Draw new route
  routeLine = L.polyline(routePoints, {
    color: '#3388ff',
    weight: 3,
    opacity: 0.7
  }).addTo(map)

  // Fit map to route bounds
  map.fitBounds(routeLine.getBounds(), { padding: [50, 50] })
}

const updateCyclistPositions = () => {
  if (!map) return

  props.positions.forEach(position => {
    const { cyclistId, location, speed } = position
    
    if (!location) return

    let marker = cyclistMarkers.get(cyclistId)
    
    if (!marker) {
      // Create new marker
      const icon = L.divIcon({
        className: 'cyclist-marker-icon',
        html: `<div style="background-color: ${getCyclistColor(cyclistId)}; width: 20px; height: 20px; border-radius: 50%; border: 2px solid white;"></div>`,
        iconSize: [20, 20]
      })

      marker = L.marker([location.latitude, location.longitude], { icon })
        .addTo(map)
        .bindPopup(`${getCyclistName(cyclistId)}<br>Speed: ${speed?.toFixed(1)} km/h`)
      
      cyclistMarkers.set(cyclistId, marker)
    } else {
      // Update existing marker
      marker.setLatLng([location.latitude, location.longitude])
      marker.setPopupContent(`${getCyclistName(cyclistId)}<br>Speed: ${speed?.toFixed(1)} km/h`)
    }
  })

  // Remove markers for cyclists no longer in positions
  const currentCyclistIds = new Set(props.positions.map(p => p.cyclistId))
  cyclistMarkers.forEach((marker, cyclistId) => {
    if (!currentCyclistIds.has(cyclistId)) {
      map!.removeLayer(marker)
      cyclistMarkers.delete(cyclistId)
    }
  })
}

watch(() => props.positions, updateCyclistPositions, { deep: true })

onMounted(() => {
  initMap()
  updateCyclistPositions()
})

onUnmounted(() => {
  if (map) {
    map.remove()
    map = null
  }
})
</script>

<style scoped>
.map-container {
  position: relative;
  width: 100%;
  height: 500px;
}

.event-map {
  width: 100%;
  height: 100%;
  border-radius: 8px;
  overflow: hidden;
}

.map-legend {
  position: absolute;
  top: 10px;
  right: 10px;
  background: white;
  padding: 10px;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  z-index: 1000;
  min-width: 200px;
}

.map-legend h4 {
  margin: 0 0 10px 0;
  font-size: 14px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 5px;
  font-size: 12px;
}

.cyclist-marker {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  border: 1px solid white;
  box-shadow: 0 1px 2px rgba(0,0,0,0.2);
}

.speed {
  margin-left: auto;
  color: #666;
}
</style>