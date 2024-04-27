<script setup>
import { onMounted, ref } from 'vue'
const tasks = ref([])
async function fetchData() {
  const response = await fetch('http://localhost:8080/v1/tasks')
  const data = await response.json()
  console.log(data)
  return data
}
onMounted(async () => {
  const data = await fetchData()
  tasks.value = data
})

// ฟังก์ชันสำหรับแปลงค่า status เป็นข้อความที่เหมาะสม
function getStatusText(status) {
  switch (status) {
    case 'NO_STATUS':
      return 'No Status'
    case 'TO_DO':
      return 'To Do'
    case 'DOING':
      return 'Doing'
    case 'DONE':
      return 'Done'
    default:
      return 'Unknown Status'
  }
}
</script>

<style>
/* table {
  max-width: 12000x;
} */

/* th,
td {
  border: 2px solid #e2e8f0;
  border-radius: 10px;
} */

table {
  width: 100%;
  border-collapse: collapse;
  max-width: 1300px;
  margin: 0 auto;
}
th,
td {
  border: 2px solid #ffffff;

  text-align: left;
  padding: 8px;
  text-align: center;
}
th {
  font-size: 18px;
  background-color: #f2f2f2;
}
h1 {
  display: flex;
  justify-content: center;
  align-items: center;
}
tr {
  height: 35px;
}

body {
  background-image: url(image/img.jpg);
  background-repeat: no-repeat;
  background-size: cover;
}

.itbkk-id {
  font-size: 15px;
}
.itbkk-title {
  font-size: 15px;
}
.itbkk-status {
  font-size: 15px;
}
.itbkk-assingees {
  font-size: 15px;
}
.title-web {
  font-size: 40px;
}
/* .itbkk-item {
  border-radius: 10px;
} */
</style>

<template>
  <div class="p-10 w-screen h-screen">
    <div class="itbkk-us3 container mx-auto w-full max-w-screen-xl">
      <div class="flex justify-between items-center mb-4">
        <h1
          class="title-web text-3xl mb-5 text-slate-200 border-collapse text-center"
          style="font-weight: 600"
        >
          IT-Bangmod Kradan Kanban
        </h1>
      </div>

      <table class="w-full border-separate">
        <thead class="bg-white-100 border-b-2 border-gray-400 text-purple-900">
          <tr class="rounded-lg">
            <th class="p-3 font-bold tracking-wide">Id</th>

            <th class="p-3 font-bold tracking-wide">Title</th>

            <th class="p-3 font-bold tracking-wide">Assignees</th>

            <th class="p-3 font-bold tracking-wide">Status</th>
          </tr>
        </thead>

        <tbody class="bg-white border-b-2 border-gray-400">
          <tr class="itbkk-item" v-for="(task, index) in tasks" :key="index">
            <!-- id -->
            <td class="itbkk-id px-4 py-2 text-sm font-semibold text-center text-slate-700">
              {{ task.id }}
            </td>

            <!-- Title -->
            <td
              class="itbkk-title px-4 py-2 text-sm font-semibold text-slate-700 whitespace-trim"
              style="text-align: left"
            >
              {{ task.title }}
            </td>
            <!-- <td
              class="itbkk-title px-4 py-2 text-sm font-semibold text-slate-700"
              style="text-align: left"
            >
              {{ removeUnderscore(task.title) }}
            </td> -->

            <!-- Assignees -->
            <!-- <td
              class="itbkk-assingees px-4 py-2 text-sm font-semibold text-center"
              style="text-align: left; color: {{ displayAssignees(task.assignees) === 'Unassigned' ? 'gray' : 'black' }}; font-style: italic;"
            >
              {{ displayAssignees(task.assignees) }}
            </td> -->

            <td
              class="itbkk-assignees px-4 py-2 text-sm text-back font-semibold text-center"
              :class="{ 'text-[gray] italic': !task.assignees }"
            >
              {{ task.assignees || 'Unassigned' }}
            </td>

            <!-- Status -->

            <td
              class="itbkk-status px-4 py-2 text-sm font-bold text-white text-center border border-gray-300 rounded p-2"
              :class="{
                'bg-red-700': getStatusText(task.status) === 'No Status',
                'bg-blue-500': getStatusText(task.status) === 'To Do',
                'bg-yellow-400': getStatusText(task.status) === 'Doing',
                'bg-green-700': getStatusText(task.status) === 'Done'
              }"
            >
              {{ getStatusText(task.status) }}
            </td>
          </tr>
        </tbody>
      </table>

      <div v-if="!tasks.length">
        <p class="text-center text-gray-500 text-xl bg-white h-10 items-center flex justify-center">
          no tasks
        </p>
      </div>
    </div>
  </div>
</template>
