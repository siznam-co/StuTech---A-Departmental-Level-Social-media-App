let functions = require('firebase-functions');

let admin = require('firebase-admin');

admin.initializeApp(functions.config().firebase);

exports.pushNotification = functions.database.ref('/Posts/{pushId}').onCreate((snap, context) => {
	
	//get the postId of the person receiving the notification because we need to get their token
	const postKey = snap.child('postKey').val();
	console.log("postKey: ", postKey);
	
	//get the user id of the person who sent the message
	const senderName = snap.child('userName').val();
	console.log("senderName: ", senderName);
	
	//get the message
	const subjectName = snap.child('subjectName').val();
	console.log("subjectName: ", subjectName);
	
	//get the message id. We'll be sending this in the payload
	const userPhoto = snap.child('userPhoto').val();
	console.log("userPhoto: ", userPhoto);
	

	console.log("Construction the notification message.");
	const payload = {
		data: {
			data_type: "post",
			postKey: postKey,
			senderName: senderName,
			subjectName: subjectName,
			userPhoto: userPhoto
		}
	};

	const options = {
		priority: "high",
		timeToLive: 60 * 60 * 24 //24 hours
	};
	
	return admin.messaging().sendToTopic(subjectName, payload, options);
});
