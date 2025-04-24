<script lang="ts">
  import { generateCommands } from "$lib/api/generateCommands";
  import { onMount } from "svelte";
  import type { Command } from "$lib/types";
  import { CommandEnum } from "$lib/types";

  // State variables
  let query = "";
  let history: { query: string; timestamp: Date; commands: Command[] }[] = [];
  let commands: Command[] = [];
  let isLoading = false;
  let error = "";

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
    } catch (err) {
      error =
        err instanceof Error ? err.message : "Failed to generate commands";
      console.error(error);
    } finally {
      isLoading = false;
    }
  };

  // Helper functions for command display
  const formatCommandType = (commandType: CommandEnum) => {
    return commandType === CommandEnum.DRIVE ? "Drive" : "Roller";
  };

  const getCommandDetails = (command: Command) => {
    if (command.command_type === CommandEnum.DRIVE) {
      const driveCmd = command.command as { speed: number; rotation: number };
      return `Speed: ${driveCmd.speed}, Rotation: ${driveCmd.rotation}`;
    } else {
      const rollerCmd = command.command as {
        forward: number;
        backward: number;
      };
      return `Forward: ${rollerCmd.forward}, Backward: ${rollerCmd.backward}`;
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

  // Computed property to determine if we have any content
  $: hasCommands = commands.length > 0 || history.length > 0;
</script>

<div class="container mx-auto p-4 min-h-screen max-w-3xl mt-28 flex flex-col">
  <header class="">
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
    <div class="flex-grow flex flex-col items-center justify-center">
      <div
        class="w-full max-w-2xl bg-white rounded-lg border border-gray-200 p-6 shadow-sm"
      >
        <h2 class="text-lg font-medium mb-4">
          What would you like the robot to do?
        </h2>
        <form on:submit|preventDefault={handleSubmit} class="space-y-4">
          <textarea
            bind:value={query}
            class="w-full min-h-32 p-4 rounded-md border border-gray-200 focus:outline-none focus:ring-2 focus:ring-purple-500 resize-none"
            placeholder="Example: Drive forward for 2 seconds, then spin the intake roller for 1 second"
            disabled={isLoading}
          ></textarea>
          <div class="flex justify-end">
            <button
              type="submit"
              class="px-6 py-2 bg-gradient-to-r from-purple-600 to-blue-500 text-white rounded-md hover:opacity-90 transition-opacity disabled:opacity-50"
              disabled={isLoading || !query.trim()}
            >
              {isLoading ? "Processing..." : "Generate Commands"}
            </button>
          </div>
        </form>

        {#if error}
          <div class="mt-4 p-3 bg-red-50 text-red-700 rounded-md">
            <p>{error}</p>
          </div>
        {/if}
      </div>
    </div>
  {:else}
    <!-- Command timeline area when commands are present -->
    <div class="flex-grow mb-6">
      <!-- Current Commands Section -->
      {#if commands.length > 0}
        <section class="mb-8">
          <h2 class="text-xl font-semibold mb-4">Current Commands</h2>
          <div
            class="bg-white rounded-lg border border-gray-200 overflow-hidden"
          >
            <div class="overflow-x-auto">
              <table class="w-full">
                <thead>
                  <tr class="bg-gray-50">
                    <th class="px-4 py-3 text-left text-sm font-medium">Type</th
                    >
                    <th class="px-4 py-3 text-left text-sm font-medium"
                      >Details</th
                    >
                    <th class="px-4 py-3 text-left text-sm font-medium"
                      >Duration/Distance</th
                    >
                    <th class="px-4 py-3 text-left text-sm font-medium"
                      >Pause</th
                    >
                  </tr>
                </thead>
                <tbody>
                  {#each commands as command, i}
                    <tr class={i % 2 === 0 ? "bg-white" : "bg-gray-50"}>
                      <td class="px-4 py-3">
                        <span
                          class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-purple-100 text-purple-800"
                        >
                          {formatCommandType(command.command_type)}
                        </span>
                      </td>
                      <td class="px-4 py-3 text-sm"
                        >{getCommandDetails(command)}</td
                      >
                      <td class="px-4 py-3 text-sm">{getDuration(command)}</td>
                      <td class="px-4 py-3 text-sm"
                        >{command.pause_duration}s</td
                      >
                    </tr>
                  {/each}
                </tbody>
              </table>
            </div>
          </div>
        </section>
      {/if}

      <!-- Command History Section -->
      {#if history.length > 0}
        <section>
          <h2 class="text-xl font-semibold mb-4">Command History</h2>
          <div class="space-y-4">
            {#each history as entry, i}
              <div
                class="bg-white rounded-lg border border-gray-200 overflow-hidden"
              >
                <div
                  class="bg-gray-50 px-4 py-3 flex justify-between items-center"
                >
                  <span class="font-medium">"{entry.query}"</span>
                  <span class="text-sm text-gray-600"
                    >{formatTime(entry.timestamp)}</span
                  >
                </div>
                <div class="overflow-x-auto">
                  <table class="w-full">
                    <thead>
                      <tr class="bg-gray-50">
                        <th class="px-4 py-2 text-left text-sm font-medium"
                          >Type</th
                        >
                        <th class="px-4 py-2 text-left text-sm font-medium"
                          >Details</th
                        >
                        <th class="px-4 py-2 text-left text-sm font-medium"
                          >Duration/Distance</th
                        >
                        <th class="px-4 py-2 text-left text-sm font-medium"
                          >Pause</th
                        >
                      </tr>
                    </thead>
                    <tbody>
                      {#each entry.commands as command, j}
                        <tr class={j % 2 === 0 ? "bg-white" : "bg-gray-50"}>
                          <td class="px-4 py-2">
                            <span
                              class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-purple-100 text-purple-800"
                            >
                              {formatCommandType(command.command_type)}
                            </span>
                          </td>
                          <td class="px-4 py-2 text-sm"
                            >{getCommandDetails(command)}</td
                          >
                          <td class="px-4 py-2 text-sm"
                            >{getDuration(command)}</td
                          >
                          <td class="px-4 py-2 text-sm"
                            >{command.pause_duration}s</td
                          >
                        </tr>
                      {/each}
                    </tbody>
                  </table>
                </div>
              </div>
            {/each}
          </div>
        </section>
      {/if}
    </div>

    <!-- Fixed input area at the bottom when commands exist -->
    <div
      class="sticky bottom-0 bg-gray-100 p-4 rounded-t-lg border-t border-gray-200 shadow-md"
    >
      <form
        on:submit|preventDefault={handleSubmit}
        class="flex items-end gap-4"
      >
        <div class="flex-grow">
          <label for="query" class="text-sm font-medium mb-1 block"
            >Ask your robot to do something</label
          >
          <textarea
            id="query"
            bind:value={query}
            class="w-full min-h-16 p-3 rounded-md border border-gray-200 focus:outline-none focus:ring-2 focus:ring-purple-500 resize-none bg-white"
            placeholder="Example: Drive forward for 2 seconds"
            disabled={isLoading}
            rows="2"
          ></textarea>
        </div>
        <button
          type="submit"
          class="px-6 py-3 bg-gradient-to-r from-purple-600 to-blue-500 text-white rounded-md hover:opacity-90 transition-opacity disabled:opacity-50 whitespace-nowrap"
          disabled={isLoading || !query.trim()}
        >
          {isLoading ? "Processing..." : "Send"}
        </button>
      </form>

      {#if error}
        <div class="mt-2 p-2 bg-red-50 text-red-700 rounded-md text-sm">
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
