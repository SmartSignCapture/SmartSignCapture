using UnityEngine;
using System.Collections;
using UnityEngine.UI;

public class AndroidCommunicator : MonoBehaviour
{
    [SerializeField]
    bool debugStartAsPlayer;

	[SerializeField]
	GameObject joysticks;

	[SerializeField] 
	Text consoleText;

    [HideInInspector]
    public bool inEditorMode = true;
	
    [TextArea]
    public string resultJson = null;//"{\"sign\": {\"items\": [{\"number\": 1, \"facialExpression\": 1, \"timestamp\": 1234, \"duration\": 1234, \"leftHand\": {\"gesture\": 1, \"keyFrames\": [{\"timestamp\": 1234, \"position\": {\"x\": 1.0, \"y\": 1.0, \"z\": 1.0 } } ] }, \"rightHand\": {\"gesture\": 1, \"keyFrames\": [{\"timestamp\": 1234, \"position\": {\"x\": 1.0, \"y\": 1.0, \"z\": 1.0 } } ] } } ], \"metadata\": {\"dateOfCreation\": \"2015-01-01 12:12:12\", \"name\": \"name\", \"author\":\"name\", \"tags\": [\"tag1\", \"tag2\"] } } }";


    void Awake()
    {
        //TimelineController.Instance.SetEditorUiActive(false);
        //PlayUI.Instance.gameObject.SetActive (false);
		joysticks.SetActive (false);
    }

    void Start()
    {
		PlayUI.Instance.gameObject.SetActive (false);
        HandClickControl.Instance.SetHandlesActive(false);

#if UNITY_EDITOR
        if (debugStartAsPlayer)
          StartAsPlayer(System.IO.File.ReadAllText(@"Assets/tempJson.txt"));
      else
          StartAsEditor();
#endif
        OnLoadingDone();

		joysticks.SetActive (true);
    }


    public void StartAsPlayer(string json)
    {
        inEditorMode = false;

        Debug.LogWarning("Start as Player was called");
		Debug.LogWarning(json);
        TimelineController.Instance.SetEditorUiActive(inEditorMode);
		PlayUI.Instance.gameObject.SetActive (!inEditorMode);
		var data = TimelineItemData.GetListFromJson(json);
        HandClickControl.Instance.SetHandlesActive(inEditorMode);
        HandClickControl.Instance.Play(data.Data,false);
    }

    public void StartAsEditor()
    {
        inEditorMode = true;
		PlayUI.Instance.gameObject.SetActive (!inEditorMode);
		TimelineController.Instance.gameObject.SetActive (inEditorMode);
        HandClickControl.Instance.SetHandlesActive(inEditorMode);
        Debug.LogWarning("Start as Editor was called");
    }

	public void OnExit(){
		#if UNITY_EDITOR
		Debug.Log (resultJson);
		//if (inEditorMode)
			//System.IO.File.WriteAllText(@"Assets/tempJson.txt", resultJson);
		#endif
		#if UNITY_ANDROID && ! UNITY_EDITOR
		
		AndroidJavaClass unity = new AndroidJavaClass ("com.unity3d.player.UnityPlayer");
		AndroidJavaObject currentActivity = unity.GetStatic<AndroidJavaObject> ("currentActivity");
		
		if (inEditorMode)
			currentActivity.Call ("onEditorClosed",resultJson);
		
		else
			currentActivity.Call ("onPlayerClosed");
		#endif
	}

    public void OnLoadingDone()
    {
#if UNITY_ANDROID && !UNITY_EDITOR
		AndroidJavaClass unity = new AndroidJavaClass ("com.unity3d.player.UnityPlayer");
		AndroidJavaObject currentActivity = unity.GetStatic<AndroidJavaObject> ("currentActivity");
		currentActivity.Call ("onUnitySceneLoaded");
#endif
#if UNITY_WEBPLAYER && !UNITY_EDITOR
		Application.ExternalCall("onUnitySceneLoaded");
#endif
    }
	
}
