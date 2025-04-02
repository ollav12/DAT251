<script setup lang="ts">
import { onMounted, ref, computed } from 'vue'

type Challenge = {
  challengeID: number
  challengeTitle: string
  description: string
  rewardPoints: number
  duration: number
  challengeType: string,
  targetValue: number,
  metricUnit: string
}

type UserChallenge = {
  challengeStatusId: number
  userID: number
  challenge: {
    challengeID: number
    challengeTitle: string
    description: string
    rewardPoints: number
    duration: number
    challengeType: string
    targetValue: number
    metricUnit: string
  }
  status: string
  startedAt: string
  completedAt: string | null
  currentValue: number
  actionsCompleted: number
  currentStreak: number
  lastActionDate: string | null
  checkpoints: any[]
}

const allChallenges = ref<Challenge[]>([])
const userChallenges = ref<UserChallenge[]>([])
const loading = ref(false)
const startingChallengeId = ref<number | null>(null)

// Add sorting state
const sortField = ref('')
const sortDirection = ref('asc')

// Function to handle column sorting
function sortBy(field: string) {
  // If clicking the same field, toggle direction
  if (sortField.value === field) {
    sortDirection.value = sortDirection.value === 'asc' ? 'desc' : 'asc'
  } else {
    // New field, reset to ascending
    sortField.value = field
    sortDirection.value = 'asc'
  }
}

// Create sorted challenges using computed properties
const sortedAllChallenges = computed(() => {
  if (!sortField.value) return allChallenges.value

  return [...allChallenges.value].sort((a, b) => {
    let aValue, bValue

    // Get the correct field values based on field name
    switch (sortField.value) {
      case 'title':
        aValue = a.challengeTitle
        bValue = b.challengeTitle
        break
      case 'description':
        aValue = a.description
        bValue = b.description
        break
      case 'points':
        aValue = a.rewardPoints
        bValue = b.rewardPoints
        break
      default:
        aValue = a[sortField.value as keyof Challenge]
        bValue = b[sortField.value as keyof Challenge]
    }

    // Compare values based on direction
    if (typeof aValue === 'string') {
      return sortDirection.value === 'asc'
        ? aValue.localeCompare(bValue as string)
        : (bValue as string).localeCompare(aValue)
    } else {
      return sortDirection.value === 'asc'
        ? (aValue as number) - (bValue as number)
        : (bValue as number) - (aValue as number)
    }
  })
})

const sortedUserChallenges = computed(() => {
  if (!sortField.value) return userChallenges.value

  return [...userChallenges.value].sort((a, b) => {
    let aValue, bValue

    // Get the correct field values based on field name
    switch (sortField.value) {
      case 'title':
        aValue = a.challenge.challengeTitle
        bValue = b.challenge.challengeTitle
        break
      case 'description':
        aValue = a.challenge.description
        bValue = b.challenge.description
        break
      case 'points':
        aValue = a.challenge.rewardPoints
        bValue = b.challenge.rewardPoints
        break
      case 'status':
        aValue = a.status
        bValue = b.status
        break
      case 'progress':
        // Sort by completion percentage
        aValue = a.currentValue / a.challenge.targetValue
        bValue = b.currentValue / b.challenge.targetValue
        break
      default:
        aValue = a[sortField.value as keyof UserChallenge]
        bValue = b[sortField.value as keyof UserChallenge]
    }

    // Compare values based on direction
    if (typeof aValue === 'string') {
      return sortDirection.value === 'asc'
        ? aValue.localeCompare(bValue as string)
        : (bValue as string).localeCompare(aValue)
    } else {
      return sortDirection.value === 'asc'
        ? (aValue as number) - (bValue as number)
        : (bValue as number) - (aValue as number)
    }
  })
})

// Add this computed property after your existing computed properties
const sortedInProgressChallenges = computed(() => {
  // First filter to get only in-progress challenges
  const inProgress = userChallenges.value.filter(challenge => !isComplete(challenge))

  if (!sortField.value) return inProgress

  // Then sort them using the same logic as other tables
  return [...inProgress].sort((a, b) => {
    let aValue, bValue

    switch (sortField.value) {
      case 'title':
        aValue = a.challenge.challengeTitle
        bValue = b.challenge.challengeTitle
        break
      case 'description':
        aValue = a.challenge.description
        bValue = b.challenge.description
        break
      case 'points':
        aValue = a.challenge.rewardPoints
        bValue = b.challenge.rewardPoints
        break
      case 'status':
        aValue = a.status
        bValue = b.status
        break
      case 'progress':
        aValue = a.currentValue / a.challenge.targetValue
        bValue = b.currentValue / b.challenge.targetValue
        break
      default:
        aValue = a[sortField.value as keyof UserChallenge]
        bValue = b[sortField.value as keyof UserChallenge]
    }

    // Compare values based on direction
    if (typeof aValue === 'string') {
      return sortDirection.value === 'asc'
        ? aValue.localeCompare(bValue as string)
        : (bValue as string).localeCompare(aValue)
    } else {
      return sortDirection.value === 'asc'
        ? (aValue as number) - (bValue as number)
        : (bValue as number) - (aValue as number)
    }
  })
})

function formatStatus(status: string): string {
  if (status === 'NOT_STARTED' || status === 'IN_PROGRESS') {
    return 'STARTED'
  } else {
    return "COMPLETED"
  }
}
async function fetchChallenges(): Promise<void> {
  loading.value = true
  try {
    // Fetch all challenges
    const responseAllChallenges = await fetch('http://localhost:8080/challenges')
    const challenges = await responseAllChallenges.json()

    // Fetch user's challenges
    const userId = localStorage.getItem("userId")
    if (!userId) {
      throw new Error("User ID not found")
    }

    const responseAllUserChallenges = await fetch(`http://localhost:8080/users/${userId}/challenges`)
    const userChallengeData = await responseAllUserChallenges.json()

    // Set user challenges
    userChallenges.value = userChallengeData

    // Filter out challenges that the user has already interacted with
    const userChallengeIds = userChallengeData.map((uc: UserChallenge) => uc.challenge.challengeID)
    allChallenges.value = challenges.filter((c: Challenge) => !userChallengeIds.includes(c.challengeID))

  } catch (error) {
    console.error("Error fetching challenges:", error)
  } finally {
    loading.value = false
  }
}

function isComplete(userChallenge: UserChallenge): boolean {
  return userChallenge.status === 'COMPLETED' ||
         userChallenge.currentValue >= userChallenge.challenge.targetValue;
}


async function startChallenge(challengeID: number): Promise<void> {
  startingChallengeId.value = challengeID

  try {
    const userId = localStorage.getItem("userId")
    if (!userId) {
      throw new Error("User ID not found")
    }

    const response = await fetch(`http://localhost:8080/users/${userId}/challenges/${challengeID}/start`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      }
    })

    if (!response.ok) {
      const errorData = await response.json()
      throw new Error(errorData.error || 'Failed to start challenge')
    }

    // Refresh the challenges to update both tables
    await fetchChallenges()

  } catch (error) {
    console.error("Error starting challenge:", error)
  } finally {
    startingChallengeId.value = null
  }
}

// Add this computed property after your other computed properties
const sortedCompletedChallenges = computed(() => {
  // First filter to get only completed challenges
  const completed = userChallenges.value.filter(challenge => isComplete(challenge))

  if (!sortField.value) return completed

  // Then sort them using the same logic as other tables
  return [...completed].sort((a, b) => {
    let aValue, bValue

    switch (sortField.value) {
      case 'title':
        aValue = a.challenge.challengeTitle
        bValue = b.challenge.challengeTitle
        break
      case 'description':
        aValue = a.challenge.description
        bValue = b.challenge.description
        break
      case 'points':
        aValue = a.challenge.rewardPoints
        bValue = b.challenge.rewardPoints
        break
      case 'completedAt':
        aValue = a.completedAt || ''
        bValue = b.completedAt || ''
        break
      default:
        aValue = a[sortField.value as keyof UserChallenge]
        bValue = b[sortField.value as keyof UserChallenge]
    }

    // Compare values based on direction
    if (typeof aValue === 'string') {
      return sortDirection.value === 'asc'
        ? aValue.localeCompare(bValue as string)
        : (bValue as string).localeCompare(aValue)
    } else {
      return sortDirection.value === 'asc'
        ? (aValue as number) - (bValue as number)
        : (bValue as number) - (aValue as number)
    }
  })
})

onMounted(async () => {
  await fetchChallenges()
})
</script>

<template>
  <main>
    <h1>Challenges</h1>
    <p>Complete challenges to receive points that you can use to unlock cosmetics.</p>

    <div v-if="loading" class="loading">Loading challenges...</div>

    <div v-else class="tables-container">
      <!-- All Challenges Table -->
      <div class="table-section">
        <h2>All Challenges</h2>
        <table>
          <thead>
            <tr>
              <th>Nr</th>
              <th @click="sortBy('title')" class="sortable">
                Title
                <span v-if="sortField === 'title'" class="sort-indicator">
                  {{ sortDirection === 'asc' ? '▲' : '▼' }}
                </span>
              </th>
              <th @click="sortBy('description')" class="sortable">
                Description
                <span v-if="sortField === 'description'" class="sort-indicator">
                  {{ sortDirection === 'asc' ? '▲' : '▼' }}
                </span>
              </th>
              <th @click="sortBy('points')" class="sortable">
                Points
                <span v-if="sortField === 'points'" class="sort-indicator">
                  {{ sortDirection === 'asc' ? '▲' : '▼' }}
                </span>
              </th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="sortedAllChallenges.length === 0">
              <td colspan="5" class="empty-message">No new challenges available</td>
            </tr>
            <tr v-for="(challenge, i) in sortedAllChallenges" :key="`new-${challenge.challengeID}`">
              <td>{{ i + 1 }}</td>
              <td>{{ challenge.challengeTitle }}</td>
              <td>{{ challenge.description }}</td>
              <td>{{ challenge.rewardPoints }} points</td>
              <td>
                <button
                  @click="startChallenge(challenge.challengeID)"
                  :disabled="startingChallengeId === challenge.challengeID"
                  class="action-button"
                >
                  {{ startingChallengeId === challenge.challengeID ? 'Starting...' : 'Start' }}
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- User Challenges Table -->
      <div class="table-section">
        <h2>Your Challenges</h2>
        <table>
          <thead>
            <tr>
              <th>Nr</th>
              <th @click="sortBy('title')" class="sortable">
                Title
                <span v-if="sortField === 'title'" class="sort-indicator">
                  {{ sortDirection === 'asc' ? '▲' : '▼' }}
                </span>
              </th>
              <th @click="sortBy('description')" class="sortable">
                Description
                <span v-if="sortField === 'description'" class="sort-indicator">
                  {{ sortDirection === 'asc' ? '▲' : '▼' }}
                </span>
              </th>
              <th @click="sortBy('points')" class="sortable">
                Points
                <span v-if="sortField === 'points'" class="sort-indicator">
                  {{ sortDirection === 'asc' ? '▲' : '▼' }}
                </span>
              </th>
              <th @click="sortBy('status')" class="sortable">
                Status
                <span v-if="sortField === 'status'" class="sort-indicator">
                  {{ sortDirection === 'asc' ? '▲' : '▼' }}
                </span>
              </th>
              <th @click="sortBy('progress')" class="sortable">
                Progress
                <span v-if="sortField === 'progress'" class="sort-indicator">
                  {{ sortDirection === 'asc' ? '▲' : '▼' }}
                </span>
              </th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="sortedInProgressChallenges.length === 0">
              <td colspan="6" class="empty-message">You haven't started any challenges yet</td>
            </tr>
            <tr v-for="(userChallenge, i) in sortedInProgressChallenges" :key="`user-${userChallenge.challengeStatusId}`">
              <td>{{ i + 1 }}</td>
              <td>{{ userChallenge.challenge.challengeTitle }}</td>
              <td>{{ userChallenge.challenge.description }}</td>
              <td>{{ userChallenge.challenge.rewardPoints }} points</td>
              <td>
                <span
                  :class="['status-badge',
                    userChallenge.status === 'COMPLETED' ? 'completed' :
                    userChallenge.status === 'STARTED' ? 'started' : 'not-started']"
                >
                {{ formatStatus(userChallenge.status) }}
                </span>
              </td>
              <td>
                <div class="progress-wrapper">
                  <div class="progress-bar">
                  <div
                    class="progress-fill"
                    :style="{
                      width: `${Math.min(100, (userChallenge.currentValue / userChallenge.challenge.targetValue) * 100)}%`,
                      backgroundColor: isComplete(userChallenge) ? '#4CAF50' : '#2196F3'
                    }"
                  ></div>
                  </div>
                  <span class="progress-text">
                    {{ Math.floor(userChallenge.currentValue) }}/{{ userChallenge.challenge.targetValue }}
                    {{ userChallenge.challenge.metricUnit }}
                  </span>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

    <!-- Completed Challenges Table -->
    <div class="table-section completed-challenges">
      <h2>Your Completed Challenges</h2>
      <table>
        <thead>
          <tr>
            <th>Nr</th>
            <th @click="sortBy('title')" class="sortable">
              Title
              <span v-if="sortField === 'title'" class="sort-indicator">
                {{ sortDirection === 'asc' ? '▲' : '▼' }}
              </span>
            </th>
            <th @click="sortBy('description')" class="sortable">
              Description
              <span v-if="sortField === 'description'" class="sort-indicator">
                {{ sortDirection === 'asc' ? '▲' : '▼' }}
              </span>
            </th>
            <th @click="sortBy('points')" class="sortable">
              Points Awarded
              <span v-if="sortField === 'points'" class="sort-indicator">
                {{ sortDirection === 'asc' ? '▲' : '▼' }}
              </span>
            </th>
            <th @click="sortBy('completedAt')" class="sortable">
              Completed On
              <span v-if="sortField === 'completedAt'" class="sort-indicator">
                {{ sortDirection === 'asc' ? '▲' : '▼' }}
              </span>
            </th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="sortedCompletedChallenges.length === 0">
            <td colspan="5" class="empty-message">You haven't completed any challenges yet</td>
          </tr>
          <tr v-for="(userChallenge, i) in sortedCompletedChallenges" :key="`completed-${userChallenge.challengeStatusId}`">
            <td>{{ i + 1 }}</td>
            <td>{{ userChallenge.challenge.challengeTitle }}</td>
            <td>{{ userChallenge.challenge.description }}</td>
            <td>{{ userChallenge.challenge.rewardPoints }} points</td>
            <td>{{ userChallenge.completedAt ? new Date(userChallenge.completedAt).toLocaleDateString() : 'Recently' }}</td>
          </tr>
        </tbody>
      </table>
    </div>
    </div>
  </main>
</template>

<style scoped>
main {
  max-width: 1100px;
  margin: 0 auto;
  padding: 0 1rem;
}

h1, h2, p {
  text-align: center;
}

.loading {
  text-align: center;
  padding: 2rem;
  font-style: italic;
}

.tables-container {
  display: flex;
  flex-wrap: wrap;
  gap: 2rem;
  margin-top: 2rem;
}

.table-section {
  flex: 1;
  min-width: 450px;
}

table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 1rem;
  text-align: left;
}

th, td {
  padding: 0.75rem;
  border-bottom: 1px solid #ddd;
}

.action-button {
  padding: 0.25rem 0.75rem;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.action-button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.status-badge {
  display: inline-block;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.85rem;
  font-weight: 500;
}

.completed {
  background-color: #4CAF50;
  color: white;
}

.started {
  background-color: #2196F3;
  color: white;
}

.not-started {
  background-color: #2196F3;
  color: white;
}

.empty-message {
  text-align: center;
  font-style: italic;
  color: #666;
}

.progress-wrapper {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.progress-bar {
  height: 8px;
  background-color: #e0e0e0;
  border-radius: 4px;
  overflow: hidden;
  width: 100%;
}

.progress-fill {
  height: 100%;
  background-color: #4CAF50;
  transition: width 0.3s ease;
}

.progress-text {
  font-size: 0.85rem;
  color: #555;
}

/* Add these new styles for sorting */
.sortable {
  cursor: pointer;
  user-select: none;
  white-space: nowrap; /* Prevent text wrapping */
  position: relative;
  padding-right: 25px; /* Make space for the arrow */
}

.sortable:hover {
  background-color: #f5f5f5;
}

.sort-indicator {
  position: absolute;
  right: 8px; /* Position arrow on the right side */
  top: 50%;
  transform: translateY(-50%);
  font-size: 0.8em;
  display: inline-block;
}

th:nth-child(2) { /* Title column */
  min-width: 120px;
}

th:nth-child(3) { /* Description column */
  min-width: 180px;
}

th:nth-child(4) { /* Points column */
  min-width: 90px;
}

th:nth-child(5) { /* Status column */
  min-width: 100px;
}

th:nth-child(6) { /* Progress column */
  min-width: 150px;
}

th {
  position: relative;
  padding: 0.75rem 1rem; /* Increase horizontal padding */
}

/* Rest of your styles remain unchanged */
@media (max-width: 950px) {
  .tables-container {
    flex-direction: column;
  }
}

.tables-container {
  display: flex;
  flex-direction: column;
  gap: 2rem;
  margin-top: 2rem;
}

.table-section {
  width: 100%;
  min-width: auto;
}


/* Add responsive behavior */
@media (max-width: 1200px) {
  .table-section {
    flex: 1 1 calc(50% - 1rem);
  }
}

@media (max-width: 768px) {
  .table-section {
    flex: 1 1 100%;
  }
}
</style>
