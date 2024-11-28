var mraid = (function () {
    // Define initial state
    var state = "loading"; // Default state as per MRAID spec

    // Define supported features
    var supportedFeatures = {
        sms: false,
        tel: false,
        calendar: false,
        storePicture: false,
        inlineVideo: false,
    };

    // Placeholder for listeners
    var eventListeners = {};

    return {
        /**************************
         * Core MRAID Functions
         **************************/

        // Get the current state of the ad
        getState: function () {
            // Fetch the state from the native interface
            return window.MRAIDInterface.getState();
        },

        // Get the current placement type (interstitial or banner)
        getPlacementType: function () {
            // Assume interstitial for now; native code should update this if needed
            return "interstitial";
        },

        // Check if a specific feature is supported
        isFeatureSupported: function (feature) {
            return supportedFeatures[feature] || false;
        },

        // Register an event listener
        addEventListener: function (event, listener) {
            if (!eventListeners[event]) {
                eventListeners[event] = [];
            }
            eventListeners[event].push(listener);
        },

        // Remove an event listener
        removeEventListener: function (event, listener) {
            if (!eventListeners[event]) return;

            var index = eventListeners[event].indexOf(listener);
            if (index !== -1) {
                eventListeners[event].splice(index, 1);
            }
        },

        // Expand the ad
        expand: function () {
            // Call native expand method
            window.MRAIDInterface.expand();
        },

        // Close the ad
        close: function () {
            // Call native close method
            window.MRAIDInterface.close();
        },

        // Resize the ad (for banners, not interstitials)
        resize: function () {
            // Implement if resizing support is needed
            console.warn("Resize is not supported in this implementation.");
        },

        /**************************
         * Internal Utility Functions
         **************************/

        // Fire a ready event
        fireReadyEvent: function () {
            state = "default"; // MRAID spec: initial state after ready is "default"
            if (eventListeners["ready"]) {
                eventListeners["ready"].forEach(function (listener) {
                    listener();
                });
            }
        },

        // Fire state change event
        fireStateChangeEvent: function (newState) {
            state = newState;
            if (eventListeners["stateChange"]) {
                eventListeners["stateChange"].forEach(function (listener) {
                    listener(newState);
                });
            }
        },

        // Fire viewable change event
        fireViewableChangeEvent: function (isViewable) {
            if (eventListeners["viewableChange"]) {
                eventListeners["viewableChange"].forEach(function (listener) {
                    listener(isViewable);
                });
            }
        },

        // Fire error event
        fireErrorEvent: function (message, action) {
            if (eventListeners["error"]) {
                eventListeners["error"].forEach(function (listener) {
                    listener(message, action);
                });
            }
        },

        // Fire size change event
        fireSizeChangeEvent: function (width, height) {
            if (eventListeners["sizeChange"]) {
                eventListeners["sizeChange"].forEach(function (listener) {
                    listener(width, height);
                });
            }
        },

        /**************************
         * Native to JavaScript Callbacks
         **************************/

        // Callback from native code to update state
        setState: function (newState) {
            this.fireStateChangeEvent(newState);
        },

        // Callback from native code to update viewability
        setViewable: function (isViewable) {
            this.fireViewableChangeEvent(isViewable);
        },

        // Callback from native code to set supported features
        setSupportedFeatures: function (features) {
            supportedFeatures = features;
        },
    };
})();
