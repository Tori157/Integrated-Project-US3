<script>
import Vue from 'vue'
import Toasted from 'vue-toasted'

Vue.use(Toasted)
export default {
  methods: {
    showToast() {
      this.$toasted.show('Message', {
        theme: 'toasted-primary', // สีของ Toast Modal
        position: 'top-right', // ตำแหน่งของ Toast Modal
        duration: 3000 // ระยะเวลาที่ Toast Modal จะแสดง (มิลลิวินาที)
      })
    }
  }
}
</script>

<template>
  <div
    class="itbkk-messege bg-green-200 mr-40 px-6 py-3 mx-2 my-4 rounded-md text-lg flex items-center mx-auto max-w-lg fixed top-5 right-20 z-50"
  >
    <svg viewBox="0 0 24 24" class="text-green-600 w-5 h-5 sm:w-5 sm:h-5 mr-3">
      <path
        fill="currentColor"
        d="M12,0A12,12,0,1,0,24,12,12.014,12.014,0,0,0,12,0Zm6.927,8.2-6.845,9.289a1.011,1.011,0,0,1-1.43.188L5.764,13.769a1,1,0,1,1,1.25-1.562l4.076,3.261,6.227-8.451A1,1,0,1,1,18.927,8.2Z"
      ></path>
    </svg>
    <span class="text-green-800">The task has been successfully added.</span>
    <button
      type="button"
      class="-mx-1.5 -my-1.5 bg-green-200 text-gray-400 hover:text-gray-700 rounded-full ml-1 focus:ring-1 focus:ring-gray-300 p-1.5 hover:bg-gray-100 inline-flex items-center h-8 w-8"
      data-dismiss-target="#toast-success"
      aria-label="Close"
    >
      <span class="sr-only">Close</span>
      <svg
        class="w-3 h-3 mt-1.5 mb-1.5 ml-1"
        aria-hidden="true"
        xmlns="http://www.w3.org/2000/svg"
        fill="none"
        viewBox="0 0 14 14"
      >
        <path
          stroke="currentColor"
          stroke-linecap="round"
          stroke-linejoin="round"
          stroke-width="2"
          d="m1 1 6 6m0 0 6 6M7 7l6-6M7 7l-6 6"
        />
      </svg>
    </button>
  </div>
</template>
