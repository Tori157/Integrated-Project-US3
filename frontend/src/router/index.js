import { createRouter, createWebHistory } from 'vue-router'
// import HomeView from '../views/HomeView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    // {
    //   path: '/',
    //   name: 'home',
    //   component: '/task'
    // },
    {
      path: '/',
      redirect: '/task'
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginView.vue')
    },
    {
      path: '/task',
      name: 'tasks',
      component: () => import('../views/HomeView.vue'),
      meta: {
        requiresAuth: true
      },
      children: [
        {
          path: ':id',
          name: 'task-modaldetail',
          component: () => import('../views/TaskDetail.vue')
        },
        {
          path: ':id/edit',
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
      path: '/editstatuserror',
      name: 'statusedit-error',
      component: () => import('../views/StatusNotFound.vue')
    },
    {
      path: '/statuslist',
      name: 'statuslist',
      component: () => import('../views/StatusList.vue'),
      meta: {
        requiresAuth: true
      },
      children: [
        {
          path: 'add',
          name: 'status-addmodal',
          component: () => import('../components/AddStatus.vue')
        },
        {
          path: ':id/edit',
          name: 'status-editmodal',
          component: () => import('../components/EditStatus.vue')
        },
        {
          path: ':id/delete',
          name: 'status-deletemodal',
          component: () => import('../components/DeleteStatus.vue')
        }
      ]
    }
  ]
})

router.beforeEach((to, from, next) => {
  // Should initialize the access token every time when routing
  const accessToken = document.cookie.match(/access_token=([^;]*)/)
  // Use to.meta.requiresAuth for flexibility
  if (!accessToken && to.meta.requiresAuth) {
    next({ name: 'login' })
  } else {
    next()
  }
})

export default router
