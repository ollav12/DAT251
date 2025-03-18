<script setup lang="ts">
import { useCosmeticImages } from '@/composables/useCosmeticImages';
import { useCosmeticsStore } from '@/composables/useCosmeticsStore';

const { getImagePath } = useCosmeticImages();
const { equippedBorder, equippedProfilePicture } = useCosmeticsStore();
</script>

<template>
  <div class="profile-container">
    <div class="border" v-if="equippedBorder">
      <img :src="getImagePath(equippedBorder)" alt="Border" />
    </div>
    <div class="profile-picture">
      <img :src="getImagePath(equippedProfilePicture)" alt="Profile picture" v-if="equippedProfilePicture" />
      <div class="default-avatar" v-else></div>
    </div>
  </div>
</template>

<style scoped>
.profile-container {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 100;
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
