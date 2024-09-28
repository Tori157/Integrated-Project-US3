<template>
  <div class="bg-blue-100 px-2 text-center">
    <div class="h-screen flex flex-col justify-center items-center">
      <img src="/image/ico/error-16-svgrepo-com.svg" class="h-30 w-36 mt-1.5" />
      <h1 class="text-8xl font-extrabold text-red-500">404</h1>
      <p class="text-4xl font-medium text-gray-800">This page does not exist</p>
      <p class="text-xl text-gray-800 mt-4">The page you are looking for could not be found.</p>
      <p class="text-xl">Back to task in {{ countdown }} seconds</p>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      countdown: 3
    }
  },
  mounted() {
    this.startCountdown()
  },
  methods: {
    startCountdown() {
      const interval = setInterval(() => {
        if (this.countdown > 0) {
          this.countdown--
        } else {
          clearInterval(interval)
          // Redirect back to HomeView.vue
          this.$router.push('/boards')
        }
      }, 1000)
    }
  }
}
</script>
