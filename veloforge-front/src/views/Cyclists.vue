<template>
  <div class="container page">
    <div class="flex justify-between items-center mb-4">
      <h1>🚴‍♂️ Cyclists</h1>
      <button @click="showCreateForm = true" class="btn btn-primary">
        Create Cyclist
      </button>
    </div>

    <!-- Loading State -->
    <div v-if="cyclistsStore.loading" class="loading">
      <div class="spinner"></div>
      Loading cyclists...
    </div>

    <!-- Error State -->
    <div v-else-if="cyclistsStore.error" class="card" style="border-color: var(--danger-color)">
      <p style="color: var(--danger-color)">{{ cyclistsStore.error }}</p>
      <button @click="loadCyclists" class="btn btn-primary">Retry</button>
    </div>

    <!-- Cyclists Grid -->
    <div v-else class="grid grid-2">
      <div v-for="cyclist in cyclistsStore.cyclists" :key="cyclist.id" class="card">
        <div class="card-header">
          <h3 class="card-title">{{ cyclist.name }}</h3>
          <p class="card-subtitle">ID: {{ cyclist.id }}</p>
        </div>

        <div class="cyclist-capabilities">
          <h4>Capabilities</h4>
          <div class="grid grid-2">
            <div>
              <strong>Power (PMA):</strong> {{ cyclist.capabilities.pma }}W
            </div>
            <div>
              <strong>Endurance:</strong> {{ cyclist.capabilities.physicalEndurance }}/100
            </div>
            <div>
              <strong>Sleep Resistance:</strong> {{ cyclist.capabilities.sleepResistance }}/100
            </div>
            <div>
              <strong>Mental Resilience:</strong> {{ cyclist.capabilities.mentalResilience }}/100
            </div>
          </div>
        </div>

        <div class="cyclist-state mt-4">
          <h4>Current State</h4>
          <div class="state-bars">
            <div class="state-bar">
              <span>Energy:</span>
              <div class="bar-container">
                <div 
                  class="bar energy" 
                  :style="{ width: (cyclist.state?.energyReserve || 0) + '%' }"
                ></div>
              </div>
              <span>{{ Math.round(cyclist.state?.energyReserve || 0) }}%</span>
            </div>
            
            <div class="state-bar">
              <span>Fatigue:</span>
              <div class="bar-container">
                <div 
                  class="bar fatigue" 
                  :style="{ width: (cyclist.state?.muscularFatigue || 0) + '%' }"
                ></div>
              </div>
              <span>{{ Math.round(cyclist.state?.muscularFatigue || 0) }}%</span>
            </div>
            
            <div class="state-bar">
              <span>Hydration:</span>
              <div class="bar-container">
                <div 
                  class="bar hydration" 
                  :style="{ width: (cyclist.state?.hydration || 0) + '%' }"
                ></div>
              </div>
              <span>{{ Math.round(cyclist.state?.hydration || 0) }}%</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Empty State -->
      <div v-if="cyclistsStore.cyclists.length === 0" class="card">
        <div class="text-center">
          <h3>No Cyclists Found</h3>
          <p>Create your first cyclist to get started!</p>
          <button @click="showCreateForm = true" class="btn btn-primary">
            Create Cyclist
          </button>
        </div>
      </div>
    </div>

    <!-- Create Cyclist Modal -->
    <div v-if="showCreateForm" class="modal-overlay" @click="showCreateForm = false">
      <div class="modal" @click.stop>
        <div class="modal-header">
          <h2>Create New Cyclist</h2>
          <button @click="showCreateForm = false" class="btn btn-outline">×</button>
        </div>

        <form @submit.prevent="createCyclist" class="modal-body">
          <div class="form-group">
            <label class="form-label">Name</label>
            <input 
              v-model="newCyclist.name" 
              type="text" 
              class="form-input" 
              required 
              maxlength="50"
              placeholder="Enter cyclist name"
            />
          </div>

          <h4>Capabilities (Total points will be balanced)</h4>
          
          <div class="form-group">
            <label class="form-label">Power at VO2max (watts)</label>
            <input 
              v-model.number="newCyclist.capabilities.pma" 
              type="number" 
              class="form-input" 
              min="200" 
              max="500" 
              required 
            />
          </div>

          <div class="form-group">
            <label class="form-label">Physical Endurance (0-100)</label>
            <input 
              v-model.number="newCyclist.capabilities.physicalEndurance" 
              type="number" 
              class="form-input" 
              min="0" 
              max="100" 
              required 
            />
          </div>

          <div class="form-group">
            <label class="form-label">Sleep Resistance (0-100)</label>
            <input 
              v-model.number="newCyclist.capabilities.sleepResistance" 
              type="number" 
              class="form-input" 
              min="0" 
              max="100" 
              required 
            />
          </div>

          <div class="form-group">
            <label class="form-label">Mental Resilience (0-100)</label>
            <input 
              v-model.number="newCyclist.capabilities.mentalResilience" 
              type="number" 
              class="form-input" 
              min="0" 
              max="100" 
              required 
            />
          </div>

          <div class="modal-footer">
            <button type="button" @click="showCreateForm = false" class="btn btn-secondary">
              Cancel
            </button>
            <button type="submit" class="btn btn-primary" :disabled="cyclistsStore.loading">
              {{ cyclistsStore.loading ? 'Creating...' : 'Create Cyclist' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useCyclistsStore } from '@/stores/cyclists'
import { useNotification } from '@/composables/useNotification'
import type { CreateCyclistRequest } from '@/api'

const cyclistsStore = useCyclistsStore()
const { success, error } = useNotification()

const showCreateForm = ref(false)
const newCyclist = ref<CreateCyclistRequest>({
  name: '',
  capabilities: {
    pma: 300,
    physicalEndurance: 75,
    sleepResistance: 70,
    mentalResilience: 75
  }
})

async function loadCyclists() {
  await cyclistsStore.fetchCyclists()
}

async function createCyclist() {
  // Validate form
  if (!newCyclist.value.name || newCyclist.value.name.length < 2) {
    error('Validation Error', 'Name must be at least 2 characters long')
    return
  }
  
  // Validate capabilities
  const caps = newCyclist.value.capabilities
  if (caps.pma < 200 || caps.pma > 500) {
    error('Validation Error', 'Power must be between 200 and 500 watts')
    return
  }
  
  if (caps.physicalEndurance < 0 || caps.physicalEndurance > 100 ||
      caps.sleepResistance < 0 || caps.sleepResistance > 100 ||
      caps.mentalResilience < 0 || caps.mentalResilience > 100) {
    error('Validation Error', 'All capability values must be between 0 and 100')
    return
  }
  
  const cyclist = await cyclistsStore.createCyclist(newCyclist.value)
  if (cyclist) {
    success('Cyclist Created', `${cyclist.name} has been added successfully`)
    showCreateForm.value = false
    // Reset form
    newCyclist.value = {
      name: '',
      capabilities: {
        pma: 300,
        physicalEndurance: 75,
        sleepResistance: 70,
        mentalResilience: 75
      }
    }
  } else if (cyclistsStore.error) {
    error('Creation Failed', cyclistsStore.error)
  }
}

onMounted(() => {
  loadCyclists()
})
</script>

<style scoped>
.cyclist-capabilities,
.cyclist-state {
  margin-top: 1rem;
}

.cyclist-capabilities h4,
.cyclist-state h4 {
  margin: 0 0 0.5rem 0;
  font-size: 1rem;
  color: var(--text-primary);
}

.state-bars {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.state-bar {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.875rem;
}

.state-bar span:first-child {
  min-width: 80px;
  font-weight: 500;
}

.state-bar span:last-child {
  min-width: 40px;
  text-align: right;
  font-weight: 500;
}

.bar-container {
  flex: 1;
  height: 20px;
  background: var(--border-color);
  border-radius: 10px;
  overflow: hidden;
}

.bar {
  height: 100%;
  border-radius: 10px;
  transition: width 0.3s ease;
}

.bar.energy {
  background: linear-gradient(90deg, var(--success-color), #34d399);
}

.bar.fatigue {
  background: linear-gradient(90deg, var(--warning-color), var(--danger-color));
}

.bar.hydration {
  background: linear-gradient(90deg, #3b82f6, #06b6d4);
}

/* Modal Styles */
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
  max-width: 500px;
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
</style>