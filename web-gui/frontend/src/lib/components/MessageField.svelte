<script>
    import { onMount } from 'svelte';
    import autosize from 'autosize';
    import { Send, LoaderCircle } from "lucide-svelte";

    export let loading = false;
    export let query = '';
    export let onSubmit = () => {};
    
    let focused = false;

    onMount(() => {
        if (typeof window === 'undefined') return;
        autosize(document.querySelector('textarea'));
    });
</script>

<div class="rounded-xl bg-white border border-gray-100 shadow-sm {focused ? 'ring-purple-500 ring-2' : ''}">
    <textarea
            bind:value={query}
            on:focusout={() => (focused = false)}
            on:focusin={() => (focused = !focused)}
            class="w-full min-h-24 p-4 rounded-t-xl focus:outline-none focus:ring-none resize-none"
            placeholder="Example: Drive forward for 2 seconds, then spin the intake roller for 1 second"
            disabled={loading}
    ></textarea>
    <div class="flex justify-between items-center p-4 bg-white rounded-b-xl">
        <button
                type="submit"
                class="px-6 py-2 w-full flex items-center gap-2 bg-gradient-to-r from-purple-600 to-blue-500 text-white rounded-md hover:opacity-90 transition-opacity disabled:opacity-50"
                disabled={loading || !query.trim()}
        >
            Generate
            {#if loading}
                <LoaderCircle class="animate-spin h-5 w-5" />
            {:else}
                <Send class="h-5 w-5" />
            {/if}
            
    </div>
</div>