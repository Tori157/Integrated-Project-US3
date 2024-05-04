<script setup>
async function deleteTask() {
  try {
    const res = await fetch('http://localhost:8080/v1/tasks', {
      method: 'DELETE'
    })

    if (res.status === 200) {
      console.log('Deleted')
      // Frontend data handling here
    }
  } catch (error) {
    console.error('Error:', error)
  }
}
</script>

<template>
  <div
    v-if="modalVisible"
    class="absolute left-0 right-0 m-auto bg-slate-50 flex h-1/6 w-1/6 shadow-lg rounded-md"
  >
    <div class="flex flex-col gap-10 justify-center mx-8 mt-2">
      <h1 class="font-bold text-xl text-stone-600">Delete a Task</h1>
      <h3 class="itbkk-message text-stone-600">
        Do you want to delete the task "{{ tasks.title }}"?
      </h3>
      <div class="flex flex-wrap justify-end">
        <button class="itbkk-button-confirm px-6 py-0.5 text-green-700 rounded-lg">Confirm</button>
        <button
          @click="toggleModal"
          class="itbkk-button-cancel px-6 py-0.5 text-red-700 rounded-lg"
        >
          Cancel
        </button>
      </div>
    </div>
  </div>
</template>
