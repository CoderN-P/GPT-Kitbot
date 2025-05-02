<script lang="ts">
  import type { Command, RollerCommand, DriveCommand } from "$lib/types";
  import { CommandEnum } from "$lib/types";
  import {
    Car,
    CircleDashed,
    Gauge,
    Rotate3D,
    MoveRight,
    MoveLeft,
  } from "lucide-svelte";
  import { fly, fade, scale } from "svelte/transition";
  import { spring } from "svelte/motion";
  import { onMount, onDestroy } from "svelte";

  export let command: Command;
  export let active: boolean = true;
  export let index: number = 0;

  // Progress animation variables
  let progress = 0;
  let progressInterval: ReturnType<typeof setInterval> | null = null;
  let progressAnimationDuration = 0;
  let startTime: number | null = null;

  const formatCommandType = (commandType: CommandEnum) => {
    return commandType === CommandEnum.DRIVE ? "Drive" : "Roller";
  };

  // Generate natural language explanations for commands
  function getCommandDescription(): string {
    if (command.command_type === CommandEnum.DRIVE) {
      const driveCmd = command.command as DriveCommand;
      const speedDesc = getSpeedDescription(driveCmd.speed);
      const rotationDesc = getRotationDescription(driveCmd.rotation);

      if (Math.abs(driveCmd.rotation) < 0.1) {
        return `Moving ${speedDesc} in a straight line`;
      } else if (Math.abs(driveCmd.speed) < 0.1) {
        return `Turning ${rotationDesc}`;
      } else {
        return `Moving ${speedDesc} while turning ${rotationDesc}`;
      }
    } else {
      const rollerCmd = command.command as RollerCommand;

      if (rollerCmd.forward > 0 && rollerCmd.backward === 0) {
        return `Spinning intake roller forward${rollerCmd.forward < 0.5 ? " slowly" : rollerCmd.forward > 0.8 ? " at max speed" : ""}`;
      } else if (rollerCmd.backward > 0 && rollerCmd.forward === 0) {
        return `Spinning intake roller backward${rollerCmd.backward < 0.5 ? " slowly" : rollerCmd.backward > 0.8 ? " at max speed" : ""}`;
      } else if (rollerCmd.forward > 0 && rollerCmd.backward > 0) {
        return `Oscillating intake roller`;
      } else {
        return `Intake roller idle`;
      }
    }
  }

  function getSpeedDescription(speed: number): string {
    const absSpeed = Math.abs(speed);
    let direction = speed > 0 ? "forward" : "backward";

    if (absSpeed < 0.2) return `very slowly ${direction}`;
    if (absSpeed < 0.4) return `slowly ${direction}`;
    if (absSpeed < 0.7) return direction;
    if (absSpeed < 0.9) return `quickly ${direction}`;
    return `at max speed ${direction}`;
  }

  function getRotationDescription(rotation: number): string {
    const absRotation = Math.abs(rotation);
    let direction = rotation > 0 ? "right" : "left";

    if (absRotation < 0.2) return `slightly ${direction}`;
    if (absRotation < 0.5) return direction;
    if (absRotation < 0.8) return `sharply ${direction}`;
    return `in a tight ${direction} turn`;
  }

  function getDurationDescription(): string {
    if (!command.duration) return "";
    if (command.duration < 1) return `for a brief moment`;
    if (command.duration < 2) return `for a second`;
    if (command.duration < 5) return `for ${command.duration} seconds`;
    return `for ${command.duration} seconds`;
  }

  // Spring animation for active state
  const activeSpring = spring({ scale: 1, rotate: 0 });

  // Update spring when active state changes
  $: {
    if (active) {
      activeSpring.set({ scale: 1.02, rotate: 0 });
      setTimeout(() => activeSpring.set({ scale: 1, rotate: 0 }), 300);
    }
  }

  // Determine background color based on command type and active state
  $: bgGradient = active
    ? command.command_type === CommandEnum.DRIVE
      ? "bg-gradient-to-r from-blue-50 to-sky-50 hover:from-blue-100 hover:to-sky-100"
      : "bg-gradient-to-r from-purple-50 to-pink-50 hover:from-purple-100 hover:to-pink-100"
    : command.command_type === CommandEnum.DRIVE
      ? "bg-gradient-to-r from-blue-50/70 to-sky-50/70 hover:from-blue-100/70 hover:to-sky-100/70"
      : "bg-gradient-to-r from-purple-50/70 to-pink-50/70 hover:from-purple-100/70 hover:to-pink-100/70";

  // Determine border color based on command type and active state
  $: borderColor = active
    ? command.command_type === CommandEnum.DRIVE
      ? "border-blue-200"
      : "border-purple-200"
    : command.command_type === CommandEnum.DRIVE
      ? "border-blue-200/50"
      : "border-purple-200/50";

  // Determine text and icon colors based on active state
  $: textColor = active ? "text-gray-800" : "text-gray-600";
  $: iconBgColor = active
    ? command.command_type === CommandEnum.DRIVE
      ? "bg-blue-100"
      : "bg-purple-100"
    : command.command_type === CommandEnum.DRIVE
      ? "bg-blue-100/50"
      : "bg-purple-100/50";
  $: iconColor = active
    ? command.command_type === CommandEnum.DRIVE
      ? "text-blue-600"
      : "text-purple-600"
    : command.command_type === CommandEnum.DRIVE
      ? "text-blue-500/80"
      : "text-purple-500/80";

  // Calculate delay for entrance animation
  $: entranceDelay = index * 150;

  // Setup progress animation when component is active
  onMount(() => {
    if (active && command.duration) {
      startProgressAnimation();
    }
  });

  // Start progress animation
  function startProgressAnimation() {
    // Clean up any existing interval
    if (progressInterval !== null) {
      clearInterval(progressInterval);
    }

    // Reset progress
    progress = 0;
    startTime = Date.now();
    progressAnimationDuration = command.duration * 1000; // Convert to milliseconds

    // Update progress every 50ms
    progressInterval = setInterval(() => {
      if (startTime) {
        const elapsed = Date.now() - startTime;
        progress = Math.min(elapsed / progressAnimationDuration, 1);

        if (progress >= 1) {
          clearInterval(progressInterval as ReturnType<typeof setInterval>);
          progressInterval = null;
        }
      }
    }, 50);
  }

  // Watch for active state changes
  $: if (active && command.duration && !progressInterval) {
    startProgressAnimation();
  }

  // Clean up on component destruction
  onDestroy(() => {
    if (progressInterval !== null) {
      clearInterval(progressInterval);
    }
  });
</script>

<div
  class="rounded-xl {bgGradient} border {borderColor} shadow-sm p-4 relative overflow-hidden transition-all duration-300 hover:shadow-md"
  style="transform: scale({$activeSpring.scale}) rotate({$activeSpring.rotate}deg);"
  in:fly={{ y: 20, duration: 400, delay: entranceDelay }}
  out:fade={{ duration: 200 }}
>
  <!-- Animated progress bar -->
  {#if active && command.duration}
    <div class="absolute left-0 right-0 bottom-0 h-1 bg-gray-100">
      <div
        class="h-full {command.command_type === CommandEnum.DRIVE
          ? 'bg-blue-500'
          : 'bg-purple-500'}"
        style="width: {progress * 100}%;"
        transition:scale={{
          start: 0,
          opacity: 1,
          duration: 300,
          origin: "left",
        }}
      ></div>
    </div>
  {/if}

  <!-- Command header with icon -->
  <div
    class="flex items-center gap-2 mb-3"
    in:fade={{ delay: entranceDelay + 200, duration: 300 }}
  >
    <div class={`p-2 rounded-full ${iconBgColor}`}>
      {#if command.command_type === CommandEnum.DRIVE}
        <Car class={`h-5 w-5 ${iconColor}`} />
      {:else}
        <CircleDashed class={`h-5 w-5 ${iconColor}`} />
      {/if}
    </div>
    <span class={`font-semibold ${textColor}`}
      >{formatCommandType(command.command_type)}</span
    >

    <!-- Duration badge -->
    {#if command.duration}
      <span
        class={`ml-auto text-xs font-medium px-2 py-1 rounded-full ${active ? "bg-gray-100" : "bg-gray-100/70"} text-gray-700`}
      >
        {command.duration}s {#if active && progress > 0 && progress < 1}({Math.round(
            progress * 100
          )}%){/if}
      </span>
    {/if}
  </div>

  <!-- Natural language command description -->
  <div class="mb-2" in:fade={{ delay: entranceDelay + 300, duration: 300 }}>
    <p class={`${active ? "text-gray-800" : "text-gray-600"} font-medium`}>
      {getCommandDescription()}
      {getDurationDescription()}
    </p>
  </div>

  <!-- Technical details with subtle animations -->
  <div
    class="flex flex-col md:flex-row justify-between gap-3 text-sm"
    in:fade={{ delay: entranceDelay + 400, duration: 300 }}
  >
    <div
      class={`flex flex-row gap-2 items-center p-2 rounded-lg ${active ? "bg-white/50" : "bg-white/30"} backdrop-blur-sm`}
    >
      {#if command.command_type === CommandEnum.DRIVE}
        <Gauge class={`h-4 w-4 ${iconColor}`} />
        <span class={`${active ? "text-gray-700" : "text-gray-500"}`}
          >Speed: <span class="font-medium"
            >{(command.command as DriveCommand).speed}</span
          ></span
        >
      {:else}
        <MoveRight class={`h-4 w-4 ${iconColor}`} />
        <span class={`${active ? "text-gray-700" : "text-gray-500"}`}
          >Forward: <span class="font-medium"
            >{(command.command as RollerCommand).forward}</span
          ></span
        >
      {/if}
    </div>
    <div
      class={`flex flex-row gap-2 items-center p-2 rounded-lg ${active ? "bg-white/50" : "bg-white/30"} backdrop-blur-sm`}
    >
      {#if command.command_type === CommandEnum.DRIVE}
        <Rotate3D class={`h-4 w-4 ${iconColor}`} />
        <span class={`${active ? "text-gray-700" : "text-gray-500"}`}
          >Rotation: <span class="font-medium"
            >{(command.command as DriveCommand).rotation}</span
          ></span
        >
      {:else}
        <MoveLeft class={`h-4 w-4 ${iconColor}`} />
        <span class={`${active ? "text-gray-700" : "text-gray-500"}`}
          >Backward: <span class="font-medium"
            >{(command.command as RollerCommand).backward}</span
          ></span
        >
      {/if}
    </div>
  </div>

  <!-- Animated pulse dot for active indicator -->
  {#if active}
    <div class="absolute top-4 right-4 flex items-center justify-center">
      <span class="relative flex h-3 w-3">
        <span
          class="animate-ping absolute inline-flex h-full w-full rounded-full opacity-75
                    {command.command_type === CommandEnum.DRIVE
            ? 'bg-blue-400'
            : 'bg-purple-400'}"
        ></span>
        <span
          class="relative inline-flex rounded-full h-3 w-3
                    {command.command_type === CommandEnum.DRIVE
            ? 'bg-blue-500'
            : 'bg-purple-500'}"
        ></span>
      </span>
    </div>
  {/if}

  <!-- Pause indicator if applicable -->
  {#if command.pause_duration > 0}
    <div
      class="mt-2 flex items-center gap-2 text-xs text-gray-500"
      in:fade={{ delay: entranceDelay + 500, duration: 300 }}
    >
      <div class="h-px flex-grow bg-gray-200"></div>
      <span>Pause {command.pause_duration}s</span>
      <div class="h-px flex-grow bg-gray-200"></div>
    </div>
  {/if}
</div>
