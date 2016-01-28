//Author: Radomir Dinic BSc
using UnityEngine;
using System.Collections.Generic;
//using UnityEditor.Animations;

public class GestureAnimationController : MonoBehaviour
{
    public enum Hand { left, right }

    Animator animator;
    public List<string> sGestures_R = new List<string>();
    List<bool> bGestures_R = new List<bool>();

    public List<string> sGestures_L = new List<string>();
    List<bool> bGestures_L = new List<bool>();

    public List<string> sFaceExp = new List<string>();
    List<bool> bFaceExp = new List<bool>();

    string actualGestureR = "Take 001";
    string actualGestureL;
    string actualfaceExp;

    public static GestureAnimationController Instance { get; private set; }

    void Awake()
    {
        Instance = this;
    }

    void Start()
    {
        animator = GetComponent<Animator>();
        for (int i = 0; i < sGestures_R.Count; ++i)
            bGestures_R.Add(false);

        for (int i = 0; i < sGestures_L.Count; ++i)
            bGestures_L.Add(false);

        for (int i = 0; i < sFaceExp.Count; ++i)
            bGestures_L.Add(false);
    }

    public void DebugRandomFace()
    {
        print("switch face to: " + sFaceExp[Mathf.RoundToInt(Random.Range(0, sFaceExp.Count - 1))]);
        SwitchFaceExpression(sFaceExp[Mathf.RoundToInt(Random.Range(0, sFaceExp.Count - 1))]);
    }

    public void SetOpenHand()
    {
        SwitchGesture("Take 002", Hand.right);
    }

    public void SetFist()
    {
        SwitchGesture("Take 001", Hand.right);
    }

    public void SwitchGesture(string nextGesture, Hand side)
    {
        switch (side)
        {
            case Hand.left:
                foreach (var gesture in sGestures_L)
                {
                    if (gesture == nextGesture)
                        animator.SetBool("l_" + gesture, true);
                    else
                        animator.SetBool("l_" + gesture, false);
                    actualGestureL = nextGesture;
                }
                break;
            case Hand.right:
                foreach (var gesture in sGestures_R)
                {
                    if (gesture == nextGesture)
                        animator.SetBool("r_" + gesture, true);
                    else
                        animator.SetBool("r_" + gesture, false);
                    actualGestureR = nextGesture;
                }
                break;
            default:
                break;
        }

    }


    public void TurnIdle(bool on)
    {
        const string IDLE = "idle_2";
        actualGestureL =
        actualGestureR =
        actualfaceExp = IDLE;

        if (on)
        {
            foreach (var gesture in sGestures_R)
                animator.SetBool("r_" + gesture, false);

            foreach (var gesture in sGestures_L)
                animator.SetBool("l_" + gesture, false);

            foreach (var exp in sFaceExp)
                animator.SetBool("f_" + exp, false);
        }

        animator.SetBool(IDLE, on);
    }

    public void SwitchFaceExpression(string nextExpression)
    {
        foreach (var expression in sFaceExp)
        {
            if (expression == nextExpression)
                animator.SetBool(expression, true);
            else
                animator.SetBool(expression, false);
            actualGestureL = nextExpression;
        }

    }
}

