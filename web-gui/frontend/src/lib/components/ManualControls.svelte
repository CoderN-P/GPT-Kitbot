<script lang="ts">
  import { sendManualCommand } from "$lib/api/manualControls";
  import { toast } from "svelte-sonner";
  import {
    ChevronUp,
    ChevronDown,
    ChevronLeft,
    ChevronRight,
    Loader2,
    ArrowUpCircle,
    ArrowDownCircle,
  } from "lucide-svelte";

  export let visible = false;

  // State to track if buttons are being pressed
  let isSendingCommand = false;
  let activeButton: string | null = null;

  // Default duration for button presses
  const defaultDuration = 0.5;

  // Function to send drive commands
  async function sendDriveCommand(speed: number, rotation: number) {
    if (isSendingCommand) return;
    try {
      isSendingCommand = true;
      activeButton = `drive_${speed}_${rotation}`;
      await sendManualCommand({
        command_type: "drive",
        speed,
        rotation,
        duration: defaultDuration,
      });
    } catch (error) {
      console.error("Error sending drive command:", error);
      toast.error("Failed to send drive command");
    } finally {
      isSendingCommand = false;
      activeButton = null;
    }
  }

  // Function to send roller commands
  async function sendRollerCommand(forward: number, backward: number) {
    if (isSendingCommand) return;
    try {
      isSendingCommand = true;
      activeButton = `roller_${forward}_${backward}`;
      await sendManualCommand({
        command_type: "roller",
        forward,
        backward,
        duration: defaultDuration,
      });
    } catch (error) {
      console.error("Error sending roller command:", error);
      toast.error("Failed to send roller command");
    } finally {
      isSendingCommand = false;
      activeButton = null;
    }
  }
</script>

{#if visible}
  <div
    class="fixed bottom-28 left-1/2 transform -translate-x-1/2 z-40 bg-white rounded-xl shadow-lg border border-gray-200 p-4 backdrop-blur-md"
    transition:slide={{ duration: 300 }}
  >
    <div class="flex flex-col gap-4">
      <h3 class="text-lg font-semibold text-center text-gray-800">
        Manual Controls
      </h3>

      <!-- Drive Controls -->
      <div class="flex flex-col items-center gap-2">
        <div class="text-sm font-medium text-gray-600">Movement Controls</div>

        <div class="grid grid-cols-3 gap-2 mb-2">
          <!-- Empty space for top-left -->
          <div></div>

          <!-- Forward button -->
          <button
            class="p-4 rounded-lg bg-blue-100 hover:bg-blue-200 text-blue-700 font-medium transition-colors
                   focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:opacity-50
                   flex items-center justify-center"
            on:click={() => sendDriveCommand(0.5, 0)}
            disabled={isSendingCommand}
            aria-label="Drive Forward"
          >
            {#if activeButton === "drive_0.5_0"}
              <Loader2 class="h-6 w-6 animate-spin" />
            {:else}
              <ChevronUp class="h-6 w-6" />
            {/if}
          </button>

          <!-- Empty space for top-right -->
          <div></div>

          <!-- Left button -->
          <button
            class="p-4 rounded-lg bg-blue-100 hover:bg-blue-200 text-blue-700 font-medium transition-colors
                   focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:opacity-50
                   flex items-center justify-center"
            on:click={() => sendDriveCommand(0, 0.5)}
            disabled={isSendingCommand}
            aria-label="Turn Left"
          >
            {#if activeButton === "drive_0_0.5"}
              <Loader2 class="h-6 w-6 animate-spin" />
            {:else}
              <ChevronLeft class="h-6 w-6" />
            {/if}
          </button>

          <!-- Stop button -->
          <button
            class="p-4 rounded-lg bg-gray-100 hover:bg-gray-200 text-gray-700 font-medium transition-colors
                   focus:outline-none focus:ring-2 focus:ring-gray-500 disabled:opacity-50
                   flex items-center justify-center"
            on:click={() => sendDriveCommand(0, 0)}
            disabled={isSendingCommand}
            aria-label="Stop Movement"
          >
            {#if activeButton === "drive_0_0"}
              <Loader2 class="h-6 w-6 animate-spin" />
            {:else}
              <span class="text-sm font-bold">STOP</span>
            {/if}
          </button>

          <!-- Right button -->
          <button
            class="p-4 rounded-lg bg-blue-100 hover:bg-blue-200 text-blue-700 font-medium transition-colors
                   focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:opacity-50
                   flex items-center justify-center"
            on:click={() => sendDriveCommand(0, -0.5)}
            disabled={isSendingCommand}
            aria-label="Turn Right"
          >
            {#if activeButton === "drive_0_-0.5"}
              <Loader2 class="h-6 w-6 animate-spin" />
            {:else}
              <ChevronRight class="h-6 w-6" />
            {/if}
          </button>

          <!-- Empty space for bottom-left -->
          <div></div>

          <!-- Backward button -->
          <button
            class="p-4 rounded-lg bg-blue-100 hover:bg-blue-200 text-blue-700 font-medium transition-colors
                   focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:opacity-50
                   flex items-center justify-center"
            on:click={() => sendDriveCommand(-0.5, 0)}
            disabled={isSendingCommand}
            aria-label="Drive Backward"
          >
            {#if activeButton === "drive_-0.5_0"}
              <Loader2 class="h-6 w-6 animate-spin" />
            {:else}
              <ChevronDown class="h-6 w-6" />
            {/if}
          </button>

          <!-- Empty space for bottom-right -->
          <div></div>
        </div>
      </div>

      <!-- Roller Controls -->
      <div class="flex flex-col items-center gap-2 mt-2">
        <div class="text-sm font-medium text-gray-600">Roller Controls</div>

        <div class="flex gap-4">
          <!-- Intake button -->
          <button
            class="px-6 py-4 rounded-lg bg-purple-100 hover:bg-purple-200 text-purple-700 font-medium transition-colors
                   focus:outline-none focus:ring-2 focus:ring-purple-500 disabled:opacity-50
                   flex items-center gap-2"
            on:click={() => sendRollerCommand(0, 1)}
            disabled={isSendingCommand}
            aria-label="Intake"
          >
            {#if activeButton === "roller_0_1"}
              <Loader2 class="h-5 w-5 animate-spin" />
            {:else}
              <ArrowDownCircle class="h-5 w-5" />
            {/if}
            <span>Intake</span>
          </button>

          <!-- Shoot button -->
          <button
            class="px-6 py-4 rounded-lg bg-purple-100 hover:bg-purple-200 text-purple-700 font-medium transition-colors
                   focus:outline-none focus:ring-2 focus:ring-purple-500 disabled:opacity-50
                   flex items-center gap-2"
            on:click={() => sendRollerCommand(1, 0)}
            disabled={isSendingCommand}
            aria-label="Shoot"
          >
            {#if activeButton === "roller_1_0"}
              <Loader2 class="h-5 w-5 animate-spin" />
            {:else}
              <ArrowUpCircle class="h-5 w-5" />
            {/if}
            <span>Shoot</span>
          </button>
        </div>
      </div>
    </div>
  </div>
{/if}
