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
      redirect: 'board'
    },
    {
      path: '/boards',
      name: 'board',
      component: () => import('../views/BoardView.vue')
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginView.vue')
    },
    {
      path: '/boards/:id/tasks',
      name: 'tasks',
      component: () => import('../views/TaskView.vue'),
      children: [
        {
          path: '/tasks/:id/',
          name: 'task-modaldetail',
          component: () => import('../components/TaskDetail.vue')
        },
        {
          path: '/tasks/:id/edit',
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
      path: '/taskerror',
      name: 'task-error',
      component: () => import('../components/otherpage/NotFound.vue')
    },
    {
      path: '/editerror',
      name: 'taskedite-error',
      component: () => import('../components/otherpage/EditNotFound.vue')
    },
    {
      path: '/editstatuserror',
      name: 'statusedit-error',
      component: () => import('../components/otherpage/StatusNotFound.vue')
    },
    {
      path: '/boards/:id/status',
      name: 'statuslist',
      component: () => import('../views/StatusList.vue'),
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
  const accessToken = document.cookie.match(/access_token=([^;]*)/)
  if (!accessToken && to.path !== '/login') {
    next({ name: 'login' })
  } else {
    next()
  }
})

export default router
