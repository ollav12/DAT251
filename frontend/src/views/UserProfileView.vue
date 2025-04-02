<script setup lang="ts">
import { useCosmeticImages } from '@/composables/useCosmeticImages';
import { useCosmeticsStore } from '@/composables/useCosmeticsStore';
import { onMounted } from 'vue';

const { getImagePath } = useCosmeticImages();
const { equippedBorder, equippedProfilePicture, userPoints, fetchUserPoints } = useCosmeticsStore();

onMounted(async () => {
  await fetchUserPoints();
});
</script>

<template>
  <div class="profile-section">
    <div class="points-display">
      <span class="point-value">{{ userPoints }}</span> points
    </div>

    <div class="profile-container">
      <div class="border" v-if="equippedBorder">
        <img :src="getImagePath(equippedBorder)" alt="Border" />
      </div>
      <div class="profile-picture">
        <img :src="getImagePath(equippedProfilePicture)" alt="Profile picture" v-if="equippedProfilePicture" />
        <div class="default-avatar" v-else></div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.profile-section {
  display: flex;
  align-items: center;
  position: absolute;
  top: 20px;
  right: 20px;
  z-index: 100;
}

.points-display {
  background-color: #444;
  color: white;
  padding: 0.4rem 0.8rem;
  border-radius: 5px;
  margin-right: 15px;
  font-size: 0.9rem;
}

.point-value {
  color: #ffcc00;
  font-weight: bold;
  font-size: 1.1rem;
}

.profile-container {
  position: relative;
}

.border {
  position: absolute;
  width: 70px;
  height: 70px;
  top: -5px;
  left: -5px;
}

.border img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.profile-picture {
  width: 60px;
  height: 60px;
  overflow: hidden;
  background-color: #444;
}

.profile-picture img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.default-avatar {
  width: 100%;
  height: 100%;
  background-color: #777;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
