<template>
  <div class="fixed inset-0 w-screen h-screen flex items-center justify-center overflow-hidden">
    <div class="relative w-full h-full flex items-center justify-center">
      <!-- Background Video -->
      <video
      src="/src/assets/bg.mp4"
      autoplay
      loop
      muted
      playsinline
      class="absolute inset-0 w-full h-full object-cover rounded-[3rem] border-8 border-white"
    ></video>
      <!-- Overlay Content -->
      <div
        class="relative w-full h-full flex flex-col items-center justify-center text-center px-8"
      >
        <h1
          class="text-5xl md:text-6xl font-extrabold text-white drop-shadow-lg mb-6 leading-[1.6] pb-2"
          style="background: linear-gradient(90deg,#fff,#b7ffe7,#fff); background-clip: text; -webkit-background-clip: text; -webkit-text-fill-color: transparent;"
        >
          Fixed Deposits, Reimagined.
        </h1>
        <p class="text-base md:text-lg text-white/70 mb-8">
          The only FD dashboard that's as smart as you are.<br>
          No paperwork. No drama. Just pure financial glow-up.
        </p>
        <button
          @click="goToLogin"
          class="rounded-full px-[40px] py-4 bg-white text-lg font-semibold shadow-lg border-0 transition-all duration-200
                 hover:scale-105 hover:bg-gradient-to-r hover:from-green-400 hover:to-green-700"
          style="background: linear-gradient(90deg,#fff,#e0ffe7,#fff);"
        >
          Get Started
        </button>
      </div>
      <!-- Subtle Gradient Overlay -->
      <!-- <div class="absolute inset-0 rounded-[3rem] pointer-events-none"
           style="background: linear-gradient(120deg,rgba(28,69,50,0.18) 0%,rgba(69,171,124,0.18) 100%);"></div> -->
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
const router = useRouter()
const goToLogin = () => {
  router.push('/login')
}
</script>