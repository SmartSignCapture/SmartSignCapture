// Author: Radomir Dinic
using UnityEngine;
using System.Collections;
using System.Collections.Generic;

[RequireComponent(typeof(ParticleSystem))]
public class HandClickHandle : MonoBehaviour
{
    int fingerID = -1;
    bool locked = false;
    ParticleSystem particles;
    Renderer rend;
    HandClickControl control;
    Color color;
    public bool IsMoving { get; set; }
    public bool isLeftHand = false;

    void Awake()
    {
        particles = GetComponent<ParticleSystem>();
        particles.Stop();
        rend = GetComponent<Renderer>();
        control = transform.parent.gameObject.GetComponent<HandClickControl>();
        color = rend.material.GetColor("_TintColor");
        isLeftHand = transform.position.x >= 0;
    }

    void OnMouseDown()
    {

        if (TimelineController.OptionsMenuActive || control.IsPlaying)
            return;

#if UNITY_EDITOR
        locked = true;
        fingerID = 0;
        particles.Play();
        control.HandClicked(this);
        rend.material.SetColor("_TintColor", Color.red);
#endif

    }

    void OnMouseUp()
    {
#if UNITY_EDITOR
        fingerID = -1;
        particles.Stop();
        control.HandReleased(this);
        rend.material.SetColor("_TintColor", color);
        locked = false;
#endif
    }

    void Update()
    {
#if UNITY_ANDROID && !UNITY_EDITOR
        HandleTouch();
#else
        HandleKlick();
#endif
    }

    void HandleTouch()
    {
        if (TimelineController.OptionsMenuActive || control.IsPlaying)
            return;

        if (!locked && fingerID == -1)
            foreach (var touch in Input.touches)
            {
                //TODO: problem if multitouch in same frame?
                RaycastHit hit;
                if (touch.phase == TouchPhase.Began && Physics.Raycast(Camera.main.ScreenPointToRay(touch.position), out hit) && hit.transform == transform)
                {
                    fingerID = touch.fingerId;
                    locked = true;
                    particles.Play();

                    control.HandClicked(this);
                    rend.material.SetColor("_TintColor", Color.red);

                    break;
                }
            }

        if (!locked || fingerID == -1) return;

        Vector3 worldPos = transform.position;
        if (fingerID < Input.touchCount)
        {
            float hackZ = worldPos.z;
            worldPos = Input.GetTouch(fingerID).position;
            worldPos.z = Vector3.Distance(transform.position, Camera.main.transform.position);
            worldPos = Camera.main.ScreenToWorldPoint(worldPos);
            worldPos.z = Mathf.Min(0f, Mathf.Max(-1f, Input.acceleration.y.Remap(0f, -1f, -0.572f, -0.014f)));
            worldPos.z = hackZ;

            worldPos.x = isLeftHand ? Mathf.Max(worldPos.x, 0f) : worldPos.x = Mathf.Min(worldPos.x, 0f);

        }

        transform.position = worldPos;

        foreach (var touch in Input.touches)
        {
            if (touch.phase == TouchPhase.Ended && touch.fingerId == fingerID)
            {
                locked = false;
                particles.Stop();
                control.HandReleased(this);
                fingerID = -1;
                rend.material.SetColor("_TintColor", color);
            }
        }
    }

    void HandleKlick()
    {
        if (locked )
        {
            Vector3 worldPos = Input.mousePosition;
            worldPos.z = Vector3.Distance(transform.position, Camera.main.transform.position);
            worldPos = Camera.main.ScreenToWorldPoint(worldPos);
            worldPos.z = transform.position.z;
            worldPos.x = isLeftHand ? Mathf.Max(worldPos.x, 0f) : worldPos.x = Mathf.Min(worldPos.x, 0f);
            transform.position = worldPos;
        }
    }

}