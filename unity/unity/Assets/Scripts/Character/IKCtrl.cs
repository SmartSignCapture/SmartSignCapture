using UnityEngine;
using System;
using System.Collections;
using UnityStandardAssets.CrossPlatformInput;

[RequireComponent(typeof(Animator))]
public class IKCtrl : MonoBehaviour
{
    public static IKCtrl Instance { get; private set; }

    Animator animator;

    [SerializeField]
    public Transform rightHandObj = null;

    [SerializeField]
    public Transform leftHandObj = null;

    [SerializeField]
    public Transform leftHand;

    [SerializeField]
    public Transform rightHand;

    [SerializeField]
    float rotationSpeedIK = 100f;

    [SerializeField]
    float rotationSpeedBlend = 100f;

    public float tempTest = 1f;

    void Awake()
    {
        Instance = this;
    }

    void Start()
    {
        animator = GetComponent<Animator>();
    }

    TimelineItemData.Hand.Rotation rotationLeft, rotationRight;
    TimelineItemData.Hand.Rotation desiredRotationLeft, desiredRotationRight;

    //a callback for calculating IK
    void OnAnimatorIK()
    {
        //set the position and the rotation of the right hand where the external object is
        if (rightHandObj != null)
        {
            animator.SetIKPositionWeight(AvatarIKGoal.RightHand, 1.0f);
            animator.SetIKRotationWeight(AvatarIKGoal.RightHand, 1f);
            animator.SetIKPositionWeight(AvatarIKGoal.LeftHand, 1.0f);
            animator.SetIKRotationWeight(AvatarIKGoal.LeftHand, 1f);

            animator.SetIKPosition(AvatarIKGoal.RightHand, rightHandObj.position);
            animator.SetIKPosition(AvatarIKGoal.LeftHand, leftHandObj.position);

            const float maxRotationLeftIK = 180f;
            const float minRotationLeftIK = -30f;
            const float maxRotationRightIK = 20f;
            const float minRotationRightIK = -190f;

            float rightHorizontalInput = CrossPlatformInputManager.GetAxis("HorizontalRight");
            float leftHorizontalInput = CrossPlatformInputManager.GetAxis("HorizontalLeft");

            rotationLeft.IK = Mathf.Clamp(rotationLeft.IK + rotationSpeedIK * Time.deltaTime * rightHorizontalInput, minRotationLeftIK, maxRotationLeftIK);
            rotationRight.IK = Mathf.Clamp(rotationRight.IK + rotationSpeedIK * Time.deltaTime * leftHorizontalInput, minRotationRightIK, maxRotationRightIK);

            animator.SetIKRotation(AvatarIKGoal.LeftHand, animator.GetIKRotation(AvatarIKGoal.LeftHand) * Quaternion.AngleAxis(rotationLeft.IK, Vector3.forward));
            animator.SetIKRotation(AvatarIKGoal.RightHand, animator.GetIKRotation(AvatarIKGoal.RightHand) * Quaternion.AngleAxis(rotationRight.IK, Vector3.forward));

        }
    }

    public void SetRotation(AvatarIKGoal hand, TimelineItemData.Hand.Rotation rot)
    {
        switch (hand)
        {
            case AvatarIKGoal.LeftHand:
                desiredRotationLeft = rot;
                break;
            case AvatarIKGoal.RightHand:
                desiredRotationLeft = rot;
                break;
            default:
                break;
        }
    }

    

    public TimelineItemData.Hand.Rotation GetRotation(AvatarIKGoal hand)
    {
        return hand == AvatarIKGoal.LeftHand ? rotationLeft : rotationRight;
    }

    void LateUpdate()
    {
        const float maxRotation = 70f;
        const float minRotation = -70f;

        float rightVerticallInput = -CrossPlatformInputManager.GetAxis("VerticalRight");
        float leftVerticallInput = -CrossPlatformInputManager.GetAxis("VerticalLeft");

        rotationLeft.Blend = Mathf.Clamp(rotationLeft.Blend + rotationSpeedBlend * Time.deltaTime * rightVerticallInput, minRotation, maxRotation);
        rotationRight.Blend = Mathf.Clamp(rotationRight.Blend + rotationSpeedBlend * Time.deltaTime * leftVerticallInput, minRotation, maxRotation);

        rightHand.transform.localEulerAngles = Vector3.right * rotationRight.Blend;
        leftHand.transform.localEulerAngles = Vector3.right * rotationLeft.Blend;
    }
}

