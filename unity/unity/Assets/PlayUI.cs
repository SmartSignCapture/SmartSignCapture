using UnityEngine;
using System.Collections;

public class PlayUI : MonoBehaviour
{

    public static PlayUI Instance;

    void Awake()
    {
        Instance = this;
    }
}
