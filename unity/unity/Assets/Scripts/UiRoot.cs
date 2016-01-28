//Author: Radomir Dinic BSc
using UnityEngine;
using System.Collections;
using UnityEngine.UI;

public class UiRoot : MonoBehaviour 
{
    public static UiRoot instance;

    [SerializeField]
    AndroidCommunicator android;

	[SerializeField]
	TimelineController timelineController;

    [SerializeField] Text consoleText;

    string debugMsg = string.Empty;

    void Awake()
    {
        instance = this;
        Application.logMessageReceived += LogUnity;
    }

    private void LogUnity(string condition, string stackTrace, LogType type)
    {
        string msg = type + "\n" + condition + "\n" + stackTrace+"\n";
        consoleText.text = msg + consoleText.text;
    }

    public void DebugSaveExit()
    {	
		//string json = "{\"sign\":{\"items\":[ {\"number\":\"0\", \"facialExpression\":\"0\", \"timestamp\":\"0\", \"duration\":\"0.1938651\", \"leftHand\":{\"gesture\":\"0\", \"keyFrames\":[ {\"timestamp\":\"4.506111E-05\", \"position\":{\"x\":\"0.257\", \"y\":\"1.228\", \"z\":\"-0.387\"}}, {\"timestamp\":\"0.01611733\", \"position\":{\"x\":\"0.257\", \"y\":\"1.228\", \"z\":\"-0.387\"}}, {\"timestamp\":\"0.02585077\", \"position\":{\"x\":\"0.257\", \"y\":\"1.228\", \"z\":\"-0.387\"}}, {\"timestamp\":\"0.05138063\", \"position\":{\"x\":\"0.257\", \"y\":\"1.228\", \"z\":\"-0.387\"}}, {\"timestamp\":\"0.06277728\", \"position\":{\"x\":\"0.257\", \"y\":\"1.228\", \"z\":\"-0.387\"}}, {\"timestamp\":\"0.09245324\", \"position\":{\"x\":\"0.257\", \"y\":\"1.228\", \"z\":\"-0.387\"}}, {\"timestamp\":\"0.1096973\", \"position\":{\"x\":\"0.257\", \"y\":\"1.228\", \"z\":\"-0.387\"}}, {\"timestamp\":\"0.1264434\", \"position\":{\"x\":\"0.257\", \"y\":\"1.228\", \"z\":\"-0.387\"}}, {\"timestamp\":\"0.1433365\", \"position\":{\"x\":\"0.257\", \"y\":\"1.228\", \"z\":\"-0.387\"}}, {\"timestamp\":\"0.1602244\", \"position\":{\"x\":\"0.257\", \"y\":\"1.228\", \"z\":\"-0.387\"}}, {\"timestamp\":\"0.1768742\", \"position\":{\"x\":\"0.257\", \"y\":\"1.228\", \"z\":\"-0.387\"}}, {\"timestamp\":\"0.1938651\", \"position\":{\"x\":\"0.257\", \"y\":\"1.228\", \"z\":\"-0.387\"}} ]}, \"rightHand\":{\"gesture\":\"0\", \"keyFrames\":[ {\"timestamp\":\"4.506111E-05\", \"position\":{\"x\":\"-0.185\", \"y\":\"1.255\", \"z\":\"-0.387\"}}, {\"timestamp\":\"0.01611733\", \"position\":{\"x\":\"-0.1651722\", \"y\":\"1.277922\", \"z\":\"-0.5835518\"}}, {\"timestamp\":\"0.02585077\", \"position\":{\"x\":\"-0.1677999\", \"y\":\"1.27521\", \"z\":\"-0.5825924\"}}, {\"timestamp\":\"0.05138063\", \"position\":{\"x\":\"-0.173643\", \"y\":\"1.274804\", \"z\":\"-0.5816521\"}}, {\"timestamp\":\"0.06277728\", \"position\":{\"x\":\"-0.1793587\", \"y\":\"1.2759\", \"z\":\"-0.5816521\"}}, {\"timestamp\":\"0.09245324\", \"position\":{\"x\":\"-0.1846576\", \"y\":\"1.276052\", \"z\":\"-0.5864769\"}}, {\"timestamp\":\"0.1096973\", \"position\":{\"x\":\"-0.1896868\", \"y\":\"1.277801\", \"z\":\"-0.5864769\"}}, {\"timestamp\":\"0.1264434\", \"position\":{\"x\":\"-0.1959191\", \"y\":\"1.27906\", \"z\":\"-0.5819725\"}}, {\"timestamp\":\"0.1433365\", \"position\":{\"x\":\"-0.2031054\", \"y\":\"1.279494\", \"z\":\"-0.5770062\"}}, {\"timestamp\":\"0.1602244\", \"position\":{\"x\":\"-0.2105109\", \"y\":\"1.279799\", \"z\":\"-0.5807691\"}}, {\"timestamp\":\"0.1768742\", \"position\":{\"x\":\"-0.216217\", \"y\":\"1.282243\", \"z\":\"-0.5822381\"}}, {\"timestamp\":\"0.1938651\", \"position\":{\"x\":\"-0.2263322\", \"y\":\"1.287157\", \"z\":\"-0.5821019\"}} ]}} ], \"metadata\":{\"dateOfCreation\":\"2015-08-21 09:37:53\", \"name\":\"ToDo Name\", \"author\":\"ToDo Author\"}}}";
		//json = timelineController.ToJson();
		//android.StartAsPlayer (json);
		Debug.Log ("exit");
		android.resultJson = timelineController.ToJson();
		android.OnExit ();
    }


}
