<script lang="ts">
  import { generateCommands } from "$lib/api/generateCommands";
  import { onMount, tick } from "svelte";
  import type { Command } from "$lib/types";
  import { CommandEnum } from "$lib/types";
  import CommandView from "$lib/components/CommandView.svelte";
  import MessageField from "$lib/components/MessageField.svelte";
  import { fade, fly, slide } from 'svelte/transition';

  // State variables
  let query = "";
  let history: { query: string; timestamp: Date; commands: Command[] }[] = [];
  let commands: Command[] = [];
  let isLoading = false;
  let error = "";
  let commandContainerElement: HTMLElement;
  let showHistory = false;

  // Form handling
  const handleSubmit = async () => {
    if (!query.trim()) return;

    try {
      isLoading = true;
      error = "";
      const generatedCommands = await generateCommands(query);
      commands = generatedCommands;

      // Add to history
      history = [
        { query, timestamp: new Date(), commands: generatedCommands },
        ...history,
      ];

      // Clear input after successful submission
      query = "";
      
      // Scroll to the top of command container when new commands arrive
      await tick();
      if (commandContainerElement) {
        commandContainerElement.scrollTop = 0;
      }
    } catch (err) {
      error =
        err instanceof Error ? err.message : "Failed to generate commands";
      console.error(error);
    } finally {
      isLoading = false;
    }
  };
  

  const getDuration = (command: Command) => {
    if (command.duration) return `${command.duration}s`;
    if (command.distance) return `${command.distance}m`;
    return "N/A";
  };

  // Format time for display
  const formatTime = (date: Date) => {
    return new Intl.DateTimeFormat("en-US", {
      hour: "2-digit",
      minute: "2-digit",
      second: "2-digit",
    }).format(date);
  };

  const toggleHistory = () => {
    showHistory = !showHistory;
  };

  // Computed property to determine if we have any content
  $: hasCommands = commands.length > 0 || history.length > 0;
</script>

<div class="container mx-auto p-4 min-h-screen max-w-3xl flex flex-col">
  <header class="mb-8" in:fade={{ duration: 800, delay: 300 }}>
    <h1 class="text-5xl font-bold mb-2">
      FRC <span
        class="text-transparent bg-clip-text bg-gradient-to-r from-purple-600 to-blue-500"
        >Natural Language</span
      > Control
    </h1>
    <p class="text-gray-600">
      Control your robot using natural language commands
    </p>
  </header>

  {#if !hasCommands}
    <!-- Centered input when no commands are present -->
    <div class="flex-grow flex flex-col items-center justify-center w-full"
         in:fade={{ duration: 600, delay: 400 }}>
      
        <form on:submit|preventDefault={handleSubmit} class="space-y-4 w-full">
          <MessageField
            bind:query
            loading={isLoading}
          />
        </form>

        {#if error}
          <div class="mt-4 p-3 bg-red-50 text-red-700 rounded-md" in:fly={{ y: 10, duration: 300 }}>
            <p>{error}</p>
          </div>
        {/if}
      </div>
    
  {:else}
    <!-- Command timeline area when commands are present -->
    <div class="flex-grow flex flex-col overflow-hidden" in:fade>
      <!-- Current Commands Section -->
      <div class="mb-6">
        <div class="flex justify-between items-center mb-4">
          <h2 class="text-xl font-semibold" in:slide={{ duration: 300, delay: 100 }}>Current Commands</h2>
          {#if history.length > 0}
            <button 
              class="text-sm text-gray-600 hover:text-gray-900 flex items-center gap-1"
              on:click={toggleHistory}
            >
              <span>{showHistory ? 'Hide' : 'Show'} History</span>
              <span class="text-xs bg-gray-100 rounded-full px-2 py-0.5">{history.length}</span>
            </button>
          {/if}
        </div>
        
        <div class="space-y-3 max-h-[60vh] overflow-y-auto pr-2 pb-4" bind:this={commandContainerElement}>
          {#if commands.length > 0}
            {#each commands as command, i (i)}
              <CommandView {command} index={i} />
            {/each}
          {:else}
            <div class="text-center py-8 text-gray-500" in:fade>
              <p>No current commands. Enter a query below to generate commands.</p>
            </div>
          {/if}
        </div>
      </div>

      <!-- Command History Section -->
      {#if history.length > 0 && showHistory}
        <div class="mb-6" in:slide={{ duration: 400 }}>
          <h2 class="text-xl font-semibold mb-4">Command History</h2>
          <div class="space-y-6">
            {#each history as entry, historyIndex}
              <div class="border-t border-gray-100 pt-4">
                <div class="bg-gray-50 rounded-lg p-3 mb-3">
                  <div class="flex justify-between items-start">
                    <p class="font-medium text-gray-800">"{entry.query}"</p>
                    <span class="text-sm text-gray-500">{formatTime(entry.timestamp)}</span>
                  </div>
                </div>
                <div class="space-y-3 pl-3 border-l border-gray-200">
                  {#each entry.commands as command, cmdIndex}
                    <CommandView 
                      command={command} 
                      active={false} 
                      index={cmdIndex + (historyIndex * entry.commands.length)} 
                    />
                  {/each}
                </div>
              </div>
            {/each}
          </div>
        </div>
      {/if}
    </div>

    <!-- Fixed input area at the bottom when commands exist -->
    <div
      class="sticky bottom-0 bg-white/80 backdrop-blur-md pt-4 pb-6 z-10 border-t border-gray-100"
      in:fly={{ y: 20, duration: 400 }}
    >
      <form
        on:submit|preventDefault={handleSubmit}
        class="w-full"
      >
        <MessageField
          bind:query
          loading={isLoading}
        />
      </form>

      {#if error}
        <div class="mt-2 p-2 bg-red-50 text-red-700 rounded-md text-sm" in:fly={{ y: 10, duration: 300 }}>
          <p>{error}</p>
        </div>
      {/if}
    </div>
  {/if}

  <!-- Footer with version info -->
  <footer class="mt-6 text-center text-sm text-gray-500">
    <p>GPT-Kitbot Control Interface v1.0</p>
  </footer>
</div>
