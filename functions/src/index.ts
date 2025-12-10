import * as functions from "firebase-functions";
import * as admin from "firebase-admin";

admin.initializeApp();

// HTTP function to handle SOS requests
export const sendSOS = functions.https.onRequest(async (req, res) => {
  try {
    const message = req.body.message || "";
    const lat = req.body.lat || 0;
    const lon = req.body.lon || 0;

    // Save SOS data in Firestore
    await admin.firestore().collection("sos").add({
      message,
      lat,
      lon,
      timestamp: Date.now(),
    });

    res.json({success: true, msg: "SOS Stored"});
  } catch (error) {
    console.error("Error storing SOS:", error);
    res.status(500).json({success: false, error: error instanceof Error ? error.message : error});
  }
});
