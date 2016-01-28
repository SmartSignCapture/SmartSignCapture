using UnityEngine;
using UnityEngine.UI;
using System.Collections.Generic;
using UnityEngine.EventSystems;
using System.Collections;

public class TimelineController : MonoBehaviour
{
    public static TimelineController Instance { get; private set; }

    [SerializeField]
    GameObject TimelineButtonPrefab = null;

    [SerializeField]
    GameObject ScrollGroup = null;

    [SerializeField]
    RectTransform ScrollableLabel = null;

    [SerializeField]
    RectTransform ElementsOptionsPanel = null;

    [SerializeField]
    List<GameObject> GestureSelectors = new List<GameObject>();

    // Currently Highlighted Button
    [SerializeField]
    Color CurrentElementNormalColor = new Color(1.0f, 0.0f, 0.0f, 1.0f);

    [SerializeField]
    Color CurrentElementHighlightColor = new Color(0.8f, 0.0f, 0.0f, 1.0f);

    [SerializeField]
    Color CurrentElementPressedColor = new Color(0.6f, 0.0f, 0.0f, 1.0f);

    [SerializeField]
    Color CurrentElementDisabledColor = new Color(1.0f, 0.0f, 0.0f, 1.0f);

    Color OriginalNormalColor = new Color(1.0f, 1.0f, 1.0f, 1.0f);
    Color OriginalHighlightColor = new Color(245.0f / 255.0f, 245.0f / 255.0f, 245.0f / 255.0f, 1.0f);
    Color OriginalPressedColor = new Color(200.0f / 255.0f, 200.0f / 255.0f, 200.0f / 255.0f, 1.0f);
    Color OriginalDisabledColor = new Color(200.0f / 255.0f, 200.0f / 255.0f, 200.0f / 255.0f, 0.5f);

    uint currentlySelectedIndex = 0;
    RectTransform currentlySelectedRectTransform = null;
    List<TimelineButtonInformation> timelineButtonList = new List<TimelineButtonInformation>();

    [SerializeField]
    Animator JoystickAnimator;

    [HideInInspector]
    public TimelineButtonInformation CurrentlySelectedElementInfo { get; private set; }

    bool isTimelineBeingDragged = false;

    public static bool OptionsMenuActive { get; private set; }

    public Animator Anim { get; private set; }

    public bool GestureSelectorsOn
    {
        get { return GestureSelectors.Count > 0 ? GestureSelectors[0].activeInHierarchy : false; }
        set
        {
            OptionsMenuActive = value;
            //ElementsOptionsPanel.gameObject.SetActive(value);
            if (value == false && currentlySelectedRectTransform != null)
            {
                SetColorToCurrentlySelectedElement(currentlySelectedRectTransform.GetComponent<Button>(), null);
                currentlySelectedRectTransform = null;
                CurrentlySelectedElementInfo = null;
            }
            foreach (var selector in GestureSelectors)
                selector.SetActive(value);
            Anim.SetBool("InEditMode", value);
            JoystickAnimator.SetBool("InEditMode", value);
        }
    }


    void Awake()
    {
        Instance = this;
        CurrentlySelectedElementInfo = null;
        Anim = GetComponent<Animator>();
    }

    void Start()
    {
        if (TimelineButtonPrefab == null)
            Debug.LogError("TimelineController:Start: No TimelineButtonPrefab GameObject assigned!");

        if (ScrollableLabel == null)
            Debug.LogError("TimelineController:Start: No ScrollableLabel GameObject assgined!");
        else
        {
            EventTrigger eventTrigger = ScrollableLabel.GetComponent<EventTrigger>();
            if (eventTrigger != null)
            {
                // eventTrigger.OnBeginDrag()
            }
        }

        if (ElementsOptionsPanel == null)
            Debug.LogError("TimelineController:Start: No ElementsOptionsPanel GameObject assgined!");

        GestureSelectorsOn = false;
    }

    void Update()
    {
        if (Input.GetKeyDown(KeyCode.Space))
            AddGestureToTimeline(null);

        if (Input.GetKeyDown(KeyCode.Backspace))
            RemoveGestureFromTimeline(1);

        if (Input.GetKeyDown(KeyCode.Escape) && GestureSelectorsOn)
            GestureSelectorsOn = false;

        if (currentlySelectedRectTransform && ElementsOptionsPanel != null)
        {
            Vector3 panelPosition = ElementsOptionsPanel.position;
            panelPosition.x = currentlySelectedRectTransform.position.x;
            ElementsOptionsPanel.position = panelPosition;
        }
    }

    public void AddGestureToTimeline(TimelineItemData sample)
    {
        if (TimelineButtonPrefab != null && ScrollableLabel != null)
        {
            GameObject buttonObj = Instantiate(TimelineButtonPrefab) as GameObject;
            TimelineButtonInformation buttonInfo = new TimelineButtonInformation((uint)timelineButtonList.Count, sample, buttonObj);

            timelineButtonList.Add(buttonInfo);

            Button uiButton = buttonObj.GetComponent<Button>();

            // Setup EventTriggers
            uiButton.onClick.AddListener(delegate { TimelineElementClicked(buttonInfo.id); });

            uiButton.GetComponentInChildren<Text>().text = timelineButtonList.Count.ToString();
            uiButton.GetComponent<RectTransform>().SetParent(ScrollableLabel);
            uiButton.GetComponent<RectTransform>().localScale = Vector3.one;
            StartCoroutine(DelayedButtonClick(uiButton));

        }
    }

    IEnumerator DelayedButtonClick(Button b)
    {
        yield return null;
        var pointer = new PointerEventData(EventSystem.current);
        ExecuteEvents.Execute(b.gameObject, pointer, ExecuteEvents.submitHandler);
    }

    public void OnRemoveGesture()
    {
        RemoveGestureFromTimeline(currentlySelectedIndex);
    }


    public void RemoveGestureFromTimeline(uint index)
    {
        if (timelineButtonList[(int)currentlySelectedIndex].button == null)
            return;

        // Edit the buttons' Ids
        for (int i = (int)index + 1; i < timelineButtonList.Count; i++)
        {
            Button uiButton = timelineButtonList[i].button.GetComponent<Button>();
            uiButton.GetComponentInChildren<Text>().text = (i + 1).ToString();

            // Change actual id
            timelineButtonList[i].id = (uint)(i - 1);
        }

        Button oldCurrentElement = null;
        Button newCurrentElement = null;

        // Is there an element from last time that we can disable
        if (timelineButtonList.Count > 0 && timelineButtonList[(int)currentlySelectedIndex].button != null)
            oldCurrentElement = timelineButtonList[(int)currentlySelectedIndex].button.GetComponent<Button>();
        // Do we even have any elements? If not, set the current element to null and return
        else if (timelineButtonList.Count == 0)
        {
            currentlySelectedRectTransform = null;
            currentlySelectedIndex = 0;
            CurrentlySelectedElementInfo = null;
            return;
        }
        Button uiButtonToRemove = null;
        // not the first element but the last
        if (index > 0 && index == timelineButtonList.Count - 1)
        {
            uiButtonToRemove = CurrentlySelectedElementInfo.button.GetComponent<Button>();
            CurrentlySelectedElementInfo = timelineButtonList[(int)--currentlySelectedIndex];
            currentlySelectedRectTransform = CurrentlySelectedElementInfo.button.GetComponent<RectTransform>();
            SetColorToCurrentlySelectedElement(oldCurrentElement, newCurrentElement);
        }
        else if (index < timelineButtonList.Count - 1)//not the last and not the first
        {
            uiButtonToRemove = CurrentlySelectedElementInfo.button.GetComponent<Button>();

            currentlySelectedIndex = index + 1;
            CurrentlySelectedElementInfo = timelineButtonList[(int)currentlySelectedIndex];
            currentlySelectedRectTransform = CurrentlySelectedElementInfo.button.GetComponent<RectTransform>();
            currentlySelectedIndex--;
        }
        else if (index == 0 && timelineButtonList.Count == 1) //first and only element
        {
            uiButtonToRemove = CurrentlySelectedElementInfo.button.GetComponent<Button>();
        }

        if (currentlySelectedRectTransform != null)
        {
            newCurrentElement = currentlySelectedRectTransform.GetComponent<Button>();
        }

        SetColorToCurrentlySelectedElement(oldCurrentElement, newCurrentElement);

        //         Button uiButtonToRemove = CurrentlySelectedElementInfo.button.GetComponent<Button>();
        if (uiButtonToRemove != null)
        {
            Destroy(uiButtonToRemove.gameObject);
        }

        timelineButtonList.RemoveAt((int)index);


        if (timelineButtonList.Count == 0 && ElementsOptionsPanel.gameObject.activeInHierarchy)
        {
            ElementsOptionsPanel.gameObject.SetActive(false);
            GestureSelectorsOn = false;
            Debug.Log(ElementsOptionsPanel.gameObject + " off");
        }

    }

    public void TimelineElementClicked(uint index)
    {
        if (!ElementsOptionsPanel.gameObject.activeInHierarchy ||
            (ElementsOptionsPanel.gameObject.activeInHierarchy &&
            currentlySelectedRectTransform != timelineButtonList[(int)index].button.GetComponent<RectTransform>()))
        {
            HandClickControl.Instance.Play(timelineButtonList[(int)index].sample);

            GestureSelectorsOn = true;

        }
        else //deselected
        {
            GestureSelectorsOn = false;

            if (currentlySelectedRectTransform != null)
            {
                SetColorToCurrentlySelectedElement(currentlySelectedRectTransform.GetComponent<Button>(), null);
                currentlySelectedRectTransform = null;
                CurrentlySelectedElementInfo = null;
                return;
            }
        }

        Button oldCurrentElement = null;
        Button newCurrentElement = null;
        if (currentlySelectedRectTransform != null)
        {
            oldCurrentElement = currentlySelectedRectTransform.GetComponent<Button>();
        }

        currentlySelectedIndex = index;
        CurrentlySelectedElementInfo = timelineButtonList[(int)currentlySelectedIndex];

        if (ElementsOptionsPanel != null)
        {
            RectTransform rect = CurrentlySelectedElementInfo.button.GetComponent<RectTransform>();
            currentlySelectedRectTransform = rect;
            Vector3 panelPosition = ElementsOptionsPanel.position;
            panelPosition.x = rect.position.x;
            ElementsOptionsPanel.position = panelPosition;

            newCurrentElement = currentlySelectedRectTransform.GetComponent<Button>();
        }

        SetColorToCurrentlySelectedElement(oldCurrentElement, newCurrentElement);
    }


    public void PlayAll()
    {
        HandClickControl.Instance.Play(ToItemList());
    }

    private List<TimelineItemData> ToItemList()
    {
        var all = new List<TimelineItemData>();

        foreach (var btn in timelineButtonList)
            all.Add(btn.sample);

        return all;
    }

    public void OnTimelineBeginDrag()
    {
        isTimelineBeingDragged = true;
    }

    public void OnTimelineEndDrag()
    {
        isTimelineBeingDragged = false;
    }

    private void SetColorToCurrentlySelectedElement(Button old, Button current)
    {
        ColorBlock colorBlock;

        if (old != null)
        {
            colorBlock = old.colors;
            colorBlock.normalColor = OriginalNormalColor;
            colorBlock.highlightedColor = OriginalHighlightColor;
            colorBlock.pressedColor = OriginalPressedColor;
            colorBlock.disabledColor = OriginalDisabledColor;
            old.colors = colorBlock;
        }

        if (current != null)
        {
            colorBlock = current.colors;
            colorBlock.normalColor = CurrentElementNormalColor;
            colorBlock.highlightedColor = CurrentElementHighlightColor;
            colorBlock.pressedColor = CurrentElementPressedColor;
            colorBlock.disabledColor = CurrentElementPressedColor;
            current.colors = colorBlock;
        }

        if (old == null && current == null)
        {
            colorBlock = new ColorBlock();
            colorBlock.normalColor = OriginalNormalColor;
            colorBlock.highlightedColor = OriginalHighlightColor;
            colorBlock.pressedColor = OriginalPressedColor;
            colorBlock.disabledColor = OriginalDisabledColor;
            //old.colors = colorBlock;
        }
    }

    public string ToJson()
    {
        //ToDo Metadata
        var meta = new TimelineItemData.Meta()
        {
            Author = "ToDo Author",
            DateOfCreation = System.DateTime.Now,
            Name = "ToDo Name"
        };

        var container = new TimelineItemDataMeta(ToItemList(), meta);
        return TimelineItemData.GetJsonFromList(container);
    }

    public void SetEditorUiActive(bool b, bool gestureSelecrorsOn = false)
    {
        GestureSelectorsOn = gestureSelecrorsOn;
        ScrollGroup.SetActive(b);
    }


}
