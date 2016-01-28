// Author: Radomir Dinic #DATE#
using UnityEngine;
using System.Collections;

public class TimelineButtonInformation
{
    public readonly TimelineItemData sample;
    public readonly GameObject button;

	public uint id { get; set; }

    public TimelineButtonInformation(uint id, TimelineItemData sample, GameObject button)
    {
        this.id = id;
        this.sample = sample;
        this.button = button;
    }
}
