import { createRouter, createWebHistory } from 'vue-router'
// import HomeView from '../views/HomeView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: '/task'
    },
    {
      path: '/task',
      name: 'tasks',
      component: () => import('../views/HomeView.vue'),
      children: [
        {
          path: ':id',
          name: 'task-modaldetail',
          component: () => import('../views/TaskDetail.vue')
        },
        {
          path: 'add',
          name: 'task-addmodal',
          component: () => import('../components/AddTask.vue')
        },
        {
          path: 'delete',
          name: 'task-deletemodal',
          component: () => import('../components/DeleteCard.vue')
        }
      ]
    }
  ]
})

export default router
