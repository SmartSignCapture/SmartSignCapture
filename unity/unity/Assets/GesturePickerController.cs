using UnityEngine;
using UnityEngine.UI;
using System.Collections;
using System.Collections.Generic;
using System;

public class GesturePickerController : MonoBehaviour
{
    public enum PICKERTYPE { LEFT, FACE, RIGHT }

    const string    FIST = "Faust", 
                    FLAT = "Flach", 
                    TWOFINGER = "Zweifinger", 
                    ONEFINGER = "Einfinger", 
                    FIVEFINGER = "Fuenffinger";

    [SerializeField]
    RectTransform leftHandPickerContent;

    [SerializeField]
    RectTransform rightHandPickerContent;

    [SerializeField]
    RectTransform memePickerContent;

    [SerializeField]
    GameObject buttonPrefab;

    [SerializeField]
    GestureAnimationController gestureController;

    Dictionary<string, List<GameObject>> leftHandGestureDictionary = new Dictionary<string, List<GameObject>>();
    Dictionary<string, List<GameObject>> rightHandGestureDictionary = new Dictionary<string, List<GameObject>>();

    Button lastPickedLeft;
    Button lastPickedRight;
    Button lastPickedFace;

    public GesturePickerController Instance { get; private set; }

    void Awake()
    {
        Instance = this;
    }


    void Start()
    {
        leftHandGestureDictionary[FIST] = new List<GameObject>();
        leftHandGestureDictionary[FLAT] = new List<GameObject>();
        leftHandGestureDictionary[TWOFINGER] = new List<GameObject>();
        leftHandGestureDictionary[ONEFINGER] = new List<GameObject>();
        leftHandGestureDictionary[FIVEFINGER] = new List<GameObject>();

        rightHandGestureDictionary[FIST] = new List<GameObject>();
        rightHandGestureDictionary[FLAT] = new List<GameObject>();
        rightHandGestureDictionary[TWOFINGER] = new List<GameObject>();
        rightHandGestureDictionary[ONEFINGER] = new List<GameObject>();
        rightHandGestureDictionary[FIVEFINGER] = new List<GameObject>();


        foreach (var animationName in gestureController.sGestures_L)
            CreateButton(leftHandPickerContent, animationName, PICKERTYPE.LEFT);
        foreach (var animationName in gestureController.sGestures_R)
            CreateButton(rightHandPickerContent, animationName, PICKERTYPE.RIGHT);
        foreach (var animationName in gestureController.sFaceExp)
            CreateButton(memePickerContent, animationName, PICKERTYPE.FACE);

        LeftHandSubmenue(1);
        RightHandSubmenue(1);

    }

    private void CreateButton(RectTransform targetContent, string animationName, PICKERTYPE type)
    {
        GameObject button = Instantiate(buttonPrefab) as GameObject;
        button.name = "UI_Button_" + animationName;
        Text text = button.GetComponentInChildren<Text>();
        if (text)text.text = animationName;
        button.GetComponent<Button>().onClick.AddListener(delegate { OnPicked(animationName, type, button); });
        Image img = button.GetComponentsInChildren<Image>()[1];
        text.gameObject.SetActive(false);
        img.sprite = Resources.Load<Sprite>(animationName);

        if (type != PICKERTYPE.FACE)
        {
            button.SetActive(false);
            AddHandgestureToDictionary(animationName, type, button);
        }

        button.transform.SetParent(targetContent);
        button.transform.localScale = Vector3.one;
        button.transform.localEulerAngles = button.transform.position = Vector3.zero;

    }

    void AddHandgestureToDictionary(string animationname, PICKERTYPE type, GameObject obj)
    {
        var dict = type == PICKERTYPE.LEFT ? leftHandGestureDictionary : rightHandGestureDictionary;

        if (animationname.Contains(FIST))
            dict[FIST].Add(obj);
        else if (animationname.Contains(FLAT))
            dict[FLAT].Add(obj);
        else if (animationname.Contains(TWOFINGER))
            dict[TWOFINGER].Add(obj);
        else if (animationname.Contains(ONEFINGER))
            dict[ONEFINGER].Add(obj);
        else if (animationname.Contains(FIVEFINGER))
            dict[FIVEFINGER].Add(obj);
    }

    public void LeftHandSubmenue(int button)
    {
        HandSubmenue(button, leftHandGestureDictionary);
    }

    public void RightHandSubmenue(int button)
    {
        HandSubmenue(button, rightHandGestureDictionary);
    }

    void HandSubmenue(int button, Dictionary<string, List<GameObject>> dict)
    {
        foreach (var objList in dict)
        {
            foreach (var obj in objList.Value)
            {
                if (button == 1 && objList.Key == FIST)
                    obj.SetActive(true);

                else if (button == 2 && objList.Key == FLAT)
                    obj.SetActive(true);

                else if (button == 3 && objList.Key == TWOFINGER)
                    obj.SetActive(true);

                else if (button == 4 && objList.Key == ONEFINGER)
                    obj.SetActive(true);

                else if (button == 5 && objList.Key == FIVEFINGER)
                    obj.SetActive(true);

                else
                    obj.SetActive(false);
            }
        }
    }

    private void OnPicked(string animationName, PICKERTYPE type, GameObject sender)
    {
        switch (type)
        {
            case PICKERTYPE.LEFT:
                gestureController.SwitchGesture(animationName, GestureAnimationController.Hand.left);
                TimelineController.Instance.CurrentlySelectedElementInfo.sample.LeftHand.Gesture = gestureController.sGestures_L.IndexOf(animationName);
                if (lastPickedLeft) lastPickedLeft.interactable = true;
                lastPickedLeft = sender.GetComponent<Button>(); ;
                lastPickedLeft.interactable = false;
                break;
            case PICKERTYPE.FACE:
                gestureController.SwitchFaceExpression(animationName);
                TimelineController.Instance.CurrentlySelectedElementInfo.sample.FacialExpression = gestureController.sFaceExp.IndexOf(animationName);
                if (lastPickedFace) lastPickedFace.interactable = true;
                lastPickedFace = sender.GetComponent<Button>();
                lastPickedFace.interactable = false;

                break;
            case PICKERTYPE.RIGHT:
                gestureController.SwitchGesture(animationName, GestureAnimationController.Hand.right);
                TimelineController.Instance.CurrentlySelectedElementInfo.sample.RightHand.Gesture = gestureController.sGestures_R.IndexOf(animationName);
                if (lastPickedRight) lastPickedRight.interactable = true;
                lastPickedRight = sender.GetComponent<Button>();
                lastPickedRight.interactable = false;

                break;
            default:
                break;
        }

    }


}
