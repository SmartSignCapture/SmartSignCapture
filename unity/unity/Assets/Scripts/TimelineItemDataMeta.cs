using UnityEngine;
using System.Collections;
using System.Collections.Generic;

public class TimelineItemDataMeta 
{
    public TimelineItemDataMeta(List<TimelineItemData> data, TimelineItemData.Meta meta)
    {
        Data = data;
        MetaData = meta;
    }

    public List<TimelineItemData> Data { get; private set; }
    public TimelineItemData.Meta MetaData { get; private set; }
}
