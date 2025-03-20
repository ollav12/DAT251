// src/composables/useCosmeticsStore.ts
import { ref, readonly } from 'vue'

const equippedBorder = ref('')
const equippedProfilePicture = ref('')
const userPoints = ref(0)

// Initialize from localStorage
if (localStorage.getItem('equippedBorder')) {
  equippedBorder.value = localStorage.getItem('equippedBorder') || ''
}

if (localStorage.getItem('equippedProfilePicture')) {
  equippedProfilePicture.value = localStorage.getItem('equippedProfilePicture') || ''
}

export function useCosmeticsStore() {
  // Update both localStorage and reactive state
  const updateEquippedBorder = (image: string) => {
    localStorage.setItem('equippedBorder', image)
    equippedBorder.value = image
  }

  const updateEquippedProfilePicture = (image: string) => {
    localStorage.setItem('equippedProfilePicture', image)
    equippedProfilePicture.value = image
  }

  const fetchUserPoints = async () => {
    try {
      const userId = localStorage.getItem('userId')
      const response = await fetch(`http://localhost:8080/users/${userId}`, {
        credentials: 'include'
      })
      if (response.ok) {
        const userData = await response.json()
        userPoints.value = userData.points
      }
    } catch (error) {
      console.error(error)
    }
  }

  const updatePoints = (newPoints: number) => {
    userPoints.value = newPoints
  }

  return {
    equippedBorder: readonly(equippedBorder),
    equippedProfilePicture: readonly(equippedProfilePicture),
    userPoints: readonly(userPoints),
    updateEquippedBorder,
    updateEquippedProfilePicture,
    fetchUserPoints,
    updatePoints
  }
}
