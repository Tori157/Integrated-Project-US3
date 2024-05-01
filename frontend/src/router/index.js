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
        }
      ]
    }
  ]
})

export default router

// import { createRouter, createWebHistory } from 'vue-router'
// import TaskModalDetail from '../components/TaskModalDetail.vue' // ปรับเปลี่ยนที่ตั้งไฟล์ตามโครงสร้างของโปรเจคของคุณ

// const routes = [
//   {
//     path: '/task/:id',
//     name: 'task-modaldetail',
//     component: TaskModalDetail
//   }
//   // เพิ่มเส้นทางอื่น ๆ ตามต้องการ
// ]

// const router = createRouter({
//   history: createWebHistory(),
//   routes
// })

// export default router
