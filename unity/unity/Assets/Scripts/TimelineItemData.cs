using UnityEngine;
using System.Collections;
using System;
using System.Collections.Generic;
using SimpleJSON;

public class TimelineItemData
{
    public class Hand
    {
        public struct KeyFrame
        {
            public float Timestamp { get; set; }
            public Vector3 Position { get; set; }
            public KeyFrame(float timestamp, Vector3 position)
                : this()
            {
                Position = position;
                Timestamp = timestamp;
            }
            public JSONClass ToJson()
            {
                var c = new JSONClass();
                c["timestamp"] = Timestamp.ToString();
                c["position"]["x"] = Position.x.ToString();
                c["position"]["y"] = Position.y.ToString();
                c["position"]["z"] = Position.z.ToString();
                return c;
            }
        }
        public struct Rotation
        {
            public float IK { get; set; }
            public float Blend { get; set; }

            public Rotation(float ik,float blend)
                : this()
            {
                IK = ik;
                Blend = blend;
        }
            public JSONClass ToJson()
            {
                var c = new JSONClass();
                c["ik"] = IK.ToString();
                c["blend"] = Blend.ToString();
                return c;
            }
        }

        public int Gesture { get; set; }
        public Rotation rotation;
        public List<KeyFrame> KeyFrames { get; private set; }

        public Hand()
        {
            KeyFrames = new List<KeyFrame>();
        }

        public JSONClass ToJson()
        {
            var c = new JSONClass();
            c["gesture"] = Gesture.ToString();
            c["rotation"] = rotation.ToJson();

            foreach (var keyframe in KeyFrames)
            {
                c["keyFrames"][-1] = keyframe.ToJson();
            }

            return c;
        }
    }

    public class Meta
    {
        public DateTime DateOfCreation { get; set; }
        public string Name { get; set; }
        public string Author { get; set; }
        public List<string> Tags { get; private set; }

        public Meta()
        {
            Tags = new List<string>();
        }
        public JSONClass ToJson()
        {
            var c = new JSONClass();
            c["dateOfCreation"] = DateOfCreation.ToString("yyyy-MM-dd hh:mm:ss");
            c["name"] = Name;
            c["author"] = Author;

            foreach (var tag in Tags)
            {
                c["tags"][-1] = tag.ToString();
            }

            return c;
        }
    }

    public int Number { get; set; }
    public int FacialExpression { get; set; }
    public float Timestamp { get; set; }
    public float Duration { get; set; }
    public Hand RightHand { get; set; }
    public Hand LeftHand { get; set; }

    public TimelineItemData()
    {
        RightHand = new Hand();
        LeftHand = new Hand();
    }

    public static string GetJsonFromList(TimelineItemDataMeta tuple)
    {
        var sign = new JSONClass();

        foreach (var item in tuple.Data)
        {
            var i = new JSONClass();
            i["number"] = item.Number.ToString();
            i["facialExpression"] = item.FacialExpression.ToString();
            i["timestamp"] = item.Timestamp.ToString();
            i["duration"] = item.Duration.ToString();

            i["leftHand"] = item.LeftHand.ToJson();
            i["rightHand"] = item.RightHand.ToJson();
            sign["items"][-1] = i;
        }
        sign["metadata"] = tuple.MetaData.ToJson();

        var root = new JSONClass();
        root["sign"] = sign;
        return root.ToString();
    }

    public static TimelineItemDataMeta GetListFromJson(string json)
    {
        var N = JSONNode.Parse(json);
        List<TimelineItemData> items = new List<TimelineItemData>();

        foreach (JSONNode item in N["sign"]["items"].AsArray)
        {
            items.Add(Deserialize(item));
        }

        Meta m = new Meta()
        {
            DateOfCreation = DateTime.Parse(N["sign"]["metadata"]["dateOfCreation"]),
            Name = N["sign"]["metadata"]["name"],
            Author = N["sign"]["metadata"]["author"]
        };

        foreach (JSONNode tag in N["sign"]["metadata"]["tags"].AsArray)
        {
            m.Tags.Add(tag.Value);
        }

        return new TimelineItemDataMeta(items, m);
    }

    static TimelineItemData Deserialize(JSONNode item)
    {
        TimelineItemData data = new TimelineItemData()
        {
            Number = int.Parse(item["number"]),
            FacialExpression = int.Parse(item["facialExpression"]),
            Timestamp = float.Parse(item["timestamp"]),
            Duration = float.Parse(item["duration"]),

            LeftHand = new Hand()
            {
                Gesture = int.Parse(item["leftHand"]["gesture"]),
                rotation = new Hand.Rotation(float.Parse(item["leftHand"]["rotation"]["ik"]), float.Parse(item["leftHand"]["rotation"]["blend"]))
            },

            RightHand = new Hand()
            {
                Gesture = int.Parse(item["rightHand"]["gesture"]),
                rotation = new Hand.Rotation(float.Parse(item["rightHand"]["rotation"]["ik"]), float.Parse(item["rightHand"]["rotation"]["blend"]))
            }

        };

        AddKeyframesTo(item["leftHand"]["keyFrames"], data.LeftHand.KeyFrames);
        AddKeyframesTo(item["rightHand"]["keyFrames"], data.RightHand.KeyFrames);

        return data;
    }

    static void AddKeyframesTo(JSONNode n, List<Hand.KeyFrame> l)
    {
        foreach (JSONNode kf in n.AsArray)
        {
            l.Add(
                new Hand.KeyFrame(
                    float.Parse(kf["timestamp"]),
                    new Vector3(
                        float.Parse(kf["position"]["x"]),
                        float.Parse(kf["position"]["y"]),
                        float.Parse(kf["position"]["z"]))));
        }
    }


}
