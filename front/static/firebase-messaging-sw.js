// Give the service worker access to Firebase Messaging.
// Note that you can only use Firebase Messaging here. Other Firebase libraries
// are not available in the service worker.
importScripts('https://www.gstatic.com/firebasejs/8.10.1/firebase-app.js');
importScripts('https://www.gstatic.com/firebasejs/8.10.1/firebase-messaging.js');

// Initialize the Firebase app in the service worker by passing in
// your app's Firebase config object.
// https://firebase.google.com/docs/web/setup#config-object
firebase.initializeApp({
	apiKey: 'AIzaSyC2-vB7k_IQUGn1osPjD4bLfCLaJeSYPzQ',
	projectId: 'goohaeyou-9ba43',
	messagingSenderId: '421577386725',
	appId: '1:421577386725:web:5c120908373765a55e020c'
});

// Retrieve an instance of Firebase Messaging so that it can handle background
// messages.
const messaging = firebase.messaging();
