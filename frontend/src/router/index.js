import { createRouter, createWebHistory } from 'vue-router'
import Customers from '../components/Customers.vue'

const routes = [
    { path: '/', component: Customers}
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
    routes
})

export default router