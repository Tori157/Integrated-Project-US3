<template>
  <div>
    <h1>404</h1>
    <p>This page does not exist</p>
    <p>The page you are looking for could not be found.</p>
    <p>Back to task in {{ countdown }} seconds</p>
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
          this.$router.push('/task')
        }
      }, 1000)
    }
  }
}
</script>
