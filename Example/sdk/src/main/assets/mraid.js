var mraid = {
    state: "loading",
    isViewable: false,
    listeners: {},

    // Adds a listener for the given event
    addEventListener: function(event, callback) {
        if (!this.listeners[event]) {
            this.listeners[event] = [];
        }
        this.listeners[event].push(callback);
    },

    // Removes a listener for the given event
    removeEventListener: function(event, callback) {
        if (!this.listeners[event]) return;
        this.listeners[event] = this.listeners[event].filter(cb => cb !== callback);
    },

    // Fires a specific event to all registered listeners
    fireEvent: function(event, data) {
        if (this.listeners[event]) {
            this.listeners[event].forEach(function(callback) {
                callback(data);
            });
        }
    },

    // Fires the ready event
    fireReadyEvent: function() {
        this.fireEvent("ready");
    },

    // Fires the state change event
    fireStateChangeEvent: function(newState) {
        this.state = newState;
        this.fireEvent("stateChange", newState);
    },

    // Fires the viewable change event
    fireViewableChangeEvent: function(isViewable) {
        this.isViewable = isViewable;
        this.fireEvent("viewableChange", isViewable);
    },

    // Fires an error event
    fireErrorEvent: function(message, action) {
        this.fireEvent("error", { message: message, action: action });
    }
};

// Notify that the MRAID environment is ready
setTimeout(function() {
    mraid.fireReadyEvent();
}, 0);
