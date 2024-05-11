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
          path: ':id/edite',
          name: 'task-edite',
          component: () => import('../components/EditeTask.vue')
        },
        {
          path: 'add',
          name: 'task-addmodal',
          component: () => import('../components/AddTask.vue')
        },
        {
          path: ':id/delete',
          name: 'task-deletemodal',
          component: () => import('../components/DeleteTask.vue')
        }
      ]
    },
    {
      path: '/error',
      name: 'task-error',
      component: () => import('../views/NotFound.vue')
    },
    {
      path: '/editerror',
      name: 'taskedite-error',
      component: () => import('../views/EditNotFound.vue')
    },
    {
      path: '/statuslist',
      name: 'statuslist',
      component: () => import('../views/StatusList.vue'),
      children: [
        {
          path: 'add',
          name: 'status-addmodal',
          component: () => import('../components/AddStatus.vue')
        },
        {
          path: ':id/edite',
          name: 'status-editmodal',
          component: () => import('../components/EditStatus.vue')
        },
        {
          path: ':id/delete',
          name: 'status-deletemodal',
          component: () => import('../components/DeleteStatus.vue')
        },
        {
          path: 'transfer',
          name: 'status-transfermodal',
          component: () => import('../components/TransferStatus.vue')
        }
      ]
    }
  ]
})

export default router
