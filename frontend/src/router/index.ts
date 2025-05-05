import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import StatisticsView from "@/views/StatisticsView.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginView.vue'),
      meta: { hideLayout: true },
    },
    {
      path: '/signup',
      name: 'signup',
      component: () => import('../views/SignupView.vue'),
      meta: { hideLayout: true },
    },
    {
      path: '/tripestimator',
      name: 'tripestimator',
      component: () => import('../views/TripEstimatorView.vue'),
    },
    {
      path: '/leaderboard',
      name: 'leaderboard',
        component: () => import('../views/LeaderboardView.vue'),
    },
    {
      path: '/vehicles',
      name: 'vehicles',
      component: () => import('../views/VehiclesView.vue'),
    },
    {

      path: '/challenges',
      name: 'challenges',
      component: () => import('../views/ChallengesView.vue')
    },
    {
      path: '/admin',
      name: 'admin',
      component: () => import('../views/AdminView.vue'),
    },
    {
      path: '/shop',
      name: 'shop',
      component: () => import('../views/ShopView.vue'),
    },
    {
      path: '/inventory',
      name: 'inventory',
      component: () => import('../views/InventoryView.vue'),
    },
    {
      path: '/statistics',
      name: 'statistics',
      component: StatisticsView,
    }

  ],
})

export default router
