<script lang="ts">
  import "../app.css";
  import { Toaster } from "svelte-sonner";
  import { writable } from 'svelte/store';

  // Create a store for manual control visibility
  export const showManualControls = writable(false);

  let { children } = $props();
</script>

<Toaster position="top-right" />

<!-- Manual Control Toggle Button -->
<div class="fixed top-4 right-4 z-50">
  <button 
    class="bg-gray-800 hover:bg-gray-700 text-white font-medium py-2 px-4 rounded-md shadow-md transition-all flex items-center gap-2"
    on:click={() => showManualControls.update(value => !value)}
  >
    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
      <path stroke-linecap="round" stroke-linejoin="round" d="M5 12h14M12 5v14" />
    </svg>
    Manual Controls
  </button>
</div>

{@render children()}
