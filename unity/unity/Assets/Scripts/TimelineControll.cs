using UnityEngine;
using UnityEngine.UI;
using System.Collections;
using System.Collections.Generic;

public class TimelineControll : MonoBehaviour
{
    [SerializeField]
    GameObject timelineButtonPrefab;
    static uint nrButtons;
    RectTransform rectTransform;

    Dictionary<uint, TimelineButtonInformation> buttons = new Dictionary<uint, TimelineButtonInformation>();

    public static TimelineControll Instance { get; private set; }

    void Awake()
    {
        Instance = this;
    }

    void Start()
    {
        rectTransform = GetComponent<RectTransform>();
    }

    public void AddGestureToTimeline(TimelineItemData sample)
    {
        GameObject button = GameObject.Instantiate<GameObject>(timelineButtonPrefab) as GameObject;
        TimelineButtonInformation info = new TimelineButtonInformation(nrButtons, sample, button);
        buttons[nrButtons] = info;

        Button btn = button.GetComponent<Button>();
        btn.onClick.AddListener(delegate { OnButtonSelected(btn); });
        button.GetComponentInChildren<Text>().text = (nrButtons++).ToString();
        button.GetComponent<RectTransform>().SetParent(rectTransform);
    }

    public void OnButtonSelected(Button btn)
    {
        uint id = uint.Parse(btn.GetComponentInChildren<Text>().text);
        HandClickControl.Instance.Play(buttons[id].sample);
    }

    public void PlayAll()
    {
        HandClickControl.Instance.Play(ToItemList());
    }

    List<TimelineItemData> ToItemList()
    {
        var all = new List<TimelineItemData>();

        foreach (var btn in buttons)
            all.Add(btn.Value.sample);

        return all;
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

        var container = new TimelineItemDataMeta(ToItemList(),meta);
        return TimelineItemData.GetJsonFromList(container);
    }






}

