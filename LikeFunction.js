let functions = require('firebase-functions');

let admin = require('firebase-admin');

admin.initializeApp(functions.config().firebase);

exports.LikeNotification = functions.database.ref('/Likes/{postId}/{userID}').onCreate((snap, context) => {
	
	//get the postId of the person receiving the notification because we need to get their token
	const postKey = context.params.postId;
	console.log("postKey: ", postKey);

	const userID = context.params.userID;
	console.log("userID: ", userID);
	
	//get the user id of the person who sent the message
	const senderName = snap.child('userName').val();
	console.log("senderName: ", senderName);

	// Attach an asynchronous callback to read the data at our posts reference
	return admin.database().ref("/Posts/"+postKey).on("value", function(snapshot) {

		const subjectName = snapshot.child('userId').val();
		console.log("subjectName: ", subjectName);

		const userPhoto = snapshot.child('userPhoto').val();
		console.log("userPhoto: ", userPhoto);

		console.log("Construction the notification message.");
		const payload = {
			data: {
				data_type: "like",
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

	}, function (errorObject) {
	  console.log("The read failed: " + errorObject.code);
	});
	
});
