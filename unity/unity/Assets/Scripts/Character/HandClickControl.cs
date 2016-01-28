using UnityEngine;
using UnityEngine.UI;
using System.Collections;
using System.Collections.Generic;

public class HandClickControl : MonoBehaviour
{
    TimelineItemData sample;

    [SerializeField]
    AndroidCommunicator android;

    [SerializeField]
    HandClickHandle leftHand;

    [SerializeField]
    HandClickHandle rightHand;
    uint handsActive = 0;

    Vector3[] tempCache;
    float timeCache;
    bool isRecording = false;
    public int playerCount = 0;
    public bool IsPlaying { get{ return playerCount != 0; } private set { } }

    public static HandClickControl Instance { get; private set; }

    [SerializeField]
    GestureAnimationController gestureController;

    bool active = false;

    List<TimelineItemData> recordsToPlay;

    void Awake()
    {
        Instance = this;
        IsPlaying = false;
    }
    public void SetHandlesActive(bool b)
    {

        leftHand.GetComponent<Renderer>().enabled = b;
        leftHand.GetComponent<Collider>().enabled = b;
        rightHand.GetComponent<Renderer>().enabled = b;
        rightHand.GetComponent<Collider>().enabled = b;
    }

    public void HandClicked(HandClickHandle sender)
    {
        if (!active)
        {
            //gestureController.TurnIdle(false);
            active = true;
        }

        ++handsActive;
        if (!isRecording)
            StartCoroutine(Record());
    }

    public void HandReleased(HandClickHandle sender)
    {
        if (handsActive != 0)
            handsActive--;
        if (handsActive == 0f)
            isRecording = false;
    }

    public IEnumerator Record()
    {
        isRecording = true;
        TimelineItemData item = new TimelineItemData();
        float startTime = Time.realtimeSinceStartup;

        while (isRecording)
        {
            var leftKeyframe = new TimelineItemData.Hand.KeyFrame();
            var rightKeyframe = new TimelineItemData.Hand.KeyFrame();

            leftKeyframe.Position = leftHand.transform.position;
            rightKeyframe.Position = rightHand.transform.position;

            leftKeyframe.Timestamp =
            rightKeyframe.Timestamp =
            item.Duration = Time.realtimeSinceStartup - startTime;

            item.LeftHand.KeyFrames.Add(leftKeyframe);
            item.RightHand.KeyFrames.Add(rightKeyframe);

            yield return new WaitForSeconds(0.01f);
        }

        yield return null;

        if (TimelineController.Instance != null)
            TimelineController.Instance.AddGestureToTimeline(item);
    }

    public void Play(TimelineItemData rec)
    {

        gestureController.SwitchFaceExpression(gestureController.sFaceExp[rec.FacialExpression]);
        gestureController.SwitchGesture(gestureController.sGestures_L[rec.LeftHand.Gesture], GestureAnimationController.Hand.left);
        gestureController.SwitchGesture(gestureController.sGestures_R[rec.RightHand.Gesture], GestureAnimationController.Hand.right);
        IKCtrl.Instance.SetRotation(AvatarIKGoal.LeftHand, rec.LeftHand.rotation);
        IKCtrl.Instance.SetRotation(AvatarIKGoal.RightHand, rec.RightHand.rotation);

        StartCoroutine(PlayTimed(rec.LeftHand, leftHand));
        StartCoroutine(PlayTimed(rec.RightHand, rightHand));
    }

    public void Play(List<TimelineItemData> records, bool quitAfterPlaying = false)
    {
        PlayUI.Instance.gameObject.SetActive(false);
        this.recordsToPlay = records;
        StartCoroutine(PlayTimed(records, quitAfterPlaying));
    }

    IEnumerator PlayTimed(List<TimelineItemData> records, bool quitAfterPlaying)
    {
        playerCount++;
        foreach (var rec in records)
        {
            Play(rec);
            yield return new WaitForSeconds(rec.Duration);
        }

        if (quitAfterPlaying)
        {
            while (leftHand.IsMoving || rightHand.IsMoving)
            {
                yield return new WaitForSeconds(1f);
            }
#if UNITY_EDITOR
            UnityEditor.EditorApplication.isPlaying = false;
#else
			android.OnExit ();
#endif
        }
        else
        {
            while (leftHand.IsMoving || rightHand.IsMoving)
            {
                yield return new WaitForSeconds(1f);
            }

            if (!android.inEditorMode)
                PlayUI.Instance.gameObject.SetActive(true);


        }
        playerCount--;
    }


    public void Replay()
    {

        this.Play(this.recordsToPlay);

    }


    public void Close()
    {
        android.OnExit();
    }


    IEnumerator PlayTimed(TimelineItemData.Hand hand, HandClickHandle ikHandle)
    {
        playerCount++;
        while (ikHandle.IsMoving)
        {
            yield return null;
        }
        ikHandle.IsMoving = true;

        for (int i = 0; i < hand.KeyFrames.Count - 1; i++)
        {
            float duration = hand.KeyFrames[i + 1].Timestamp - hand.KeyFrames[i].Timestamp;
            Vector3 from = hand.KeyFrames[i].Position;
            Vector3 to = hand.KeyFrames[i + 1].Position;

            StartCoroutine(MoveTimed(from, to, duration, ikHandle.transform));
            yield return new WaitForSeconds(duration);
        }
        ikHandle.IsMoving = false;
        yield return null;
        playerCount--;
    }

    IEnumerator MoveTimed(Vector3 from, Vector3 to, float time, Transform handle)
    {
        float exitTime = Time.realtimeSinceStartup + time;
        yield return null;
        while (Time.realtimeSinceStartup < exitTime)
        {
            handle.position = Vector3.Lerp(from, to, Time.realtimeSinceStartup / exitTime);
            yield return null;
        }
        yield return null;
    }

}
