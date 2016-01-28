using UnityEngine;
using UnityEditor;
using UnityEditor.Animations;
using System;
using System.Collections.Generic;
using System.IO;

public class AnimatorCreator : EditorWindow
{
    #region Init
    string animPath_hands = "/Animations/BothHands";
    string animPath_face = "/Animations/Face";
    string filename = "Assets/Animations/SSC_AnimationController.controller";

    const string FACE_LAYER_NAME = "Face";
    const string BODY_LAYER_NAME = "BodyWithoutArms";
    const string LEFT_HAND_LAYER_NAME = "LeftHandOnly";
    const string RIGHT_HAND_LAYER_NAME = "RightHandOnly";

    GestureAnimationController gestureController;
    AvatarMask maskLeftHand;
    AvatarMask maskRightHand;
    AvatarMask maskFace;
    AvatarMask maskBody;
    AnimationClip idleAnimation;

    void InitDefaults()
    {
        var obj = GameObject.Find("Char2");
        if (obj)
            gestureController = obj.GetComponent<GestureAnimationController>();

        maskLeftHand = AssetDatabase.LoadAssetAtPath<AvatarMask>("Assets/Animations/Masks/LeftHandOnly.mask");
        maskRightHand = AssetDatabase.LoadAssetAtPath<AvatarMask>("Assets/Animations/Masks/RightHandOnly.mask");
        maskFace = AssetDatabase.LoadAssetAtPath<AvatarMask>("Assets/Animations/Masks/Face.mask");
        maskBody = AssetDatabase.LoadAssetAtPath<AvatarMask>("Assets/Animations/Masks/BodyWithoutArms.mask");
        idleAnimation = AssetDatabase.LoadAssetAtPath<AnimationClip>("Assets/Animations/Idle/idle_2.anim"); ;
    }
    #endregion

    #region UI
    // Add menu item named "My Window" to the Window menu
    [MenuItem("SSC/Animator Controller Wizard")]
    public static void ShowWindow()
    {
        //Show existing window instance. If one doesn't exist, make one.
        var Instance = EditorWindow.GetWindow(typeof(AnimatorCreator)) as AnimatorCreator;
        Instance.InitDefaults();
    }

    void OnGUI()
    {
        GUILayout.Label("SSC Animator Creator Wizard", EditorStyles.boldLabel);
        animPath_face = EditorGUILayout.TextField("Face Animations Folder", animPath_face);
        animPath_hands = EditorGUILayout.TextField("Hand Animations Folder", animPath_hands);
        filename = EditorGUILayout.TextField("Output Filename", filename);

        gestureController = EditorGUILayout.ObjectField("Gesture Controller", gestureController, typeof(GestureAnimationController), true) as GestureAnimationController;
        maskLeftHand = EditorGUILayout.ObjectField("Left Hand Mask", maskLeftHand, typeof(AvatarMask), true) as AvatarMask;
        maskRightHand = EditorGUILayout.ObjectField("Right Hand Mask", maskRightHand, typeof(AvatarMask), true) as AvatarMask;
        maskFace = EditorGUILayout.ObjectField("Face Mask", maskFace, typeof(AvatarMask), true) as AvatarMask;
        maskBody = EditorGUILayout.ObjectField("Body Mask", maskBody, typeof(AvatarMask), true) as AvatarMask;
        idleAnimation = EditorGUILayout.ObjectField("Idle Animation", idleAnimation, typeof(AnimationClip), true) as AnimationClip;

        bool everythingSet = !string.IsNullOrEmpty(animPath_face)
                          && !string.IsNullOrEmpty(animPath_hands)
                          && !string.IsNullOrEmpty(filename)
                          && gestureController
                          && maskLeftHand
                          && maskRightHand
                          && maskFace
                          && maskBody;

        EditorGUI.BeginDisabledGroup(!everythingSet);
        if (GUILayout.Button(everythingSet ? "Create" : "First select all Objects and Folders"))
        {
            CreateController();
        }
        EditorGUI.EndDisabledGroup();
    }
    #endregion

    void CreateController()
    {
        var controller = AnimatorController.CreateAnimatorControllerAtPath(filename);

        List<AnimationClip> handAnimationClips = new List<AnimationClip>();
        List<AnimationClip> faceAnimationClips = new List<AnimationClip>();

        foreach (var path in Directory.GetFiles(Application.dataPath + animPath_hands, "*.anim", SearchOption.AllDirectories))
        {
            string cleanPath = path.Replace(Application.dataPath + "/", "Assets/").Replace("\\", "/");
            handAnimationClips.Add(AssetDatabase.LoadAssetAtPath(cleanPath, typeof(AnimationClip)) as AnimationClip);
        }

        foreach (var path in Directory.GetFiles(Application.dataPath + animPath_face, "*.anim", SearchOption.AllDirectories))
        {
            string cleanPath = path.Replace(Application.dataPath + "/", "Assets/").Replace("\\", "/");
            faceAnimationClips.Add(AssetDatabase.LoadAssetAtPath(cleanPath, typeof(AnimationClip)) as AnimationClip);
        }

        CreateParameters(controller, handAnimationClips, "l_");
        CreateParameters(controller, handAnimationClips, "r_");
        CreateParameters(controller, faceAnimationClips);

        AnimatorControllerLayer faceLayer = CreateLayer(controller, FACE_LAYER_NAME, maskFace);
        AnimatorControllerLayer bodyLayer = CreateLayer(controller, BODY_LAYER_NAME, maskBody);
        AnimatorControllerLayer rightHandLayer = CreateLayer(controller, RIGHT_HAND_LAYER_NAME, maskRightHand);
        AnimatorControllerLayer leftHandLayer = CreateLayer(controller, LEFT_HAND_LAYER_NAME, maskLeftHand);

        AddClipsToState(faceLayer.stateMachine, faceAnimationClips);
        AddClipsToState(rightHandLayer.stateMachine, handAnimationClips, "r_");
        AddClipsToState(leftHandLayer.stateMachine, handAnimationClips, "l_");

        var state = bodyLayer.stateMachine.AddState(idleAnimation.name);
        state.motion = idleAnimation;
        bodyLayer.stateMachine.defaultState = state;

        controller.AddLayer(faceLayer);
        controller.AddLayer(bodyLayer);
        controller.AddLayer(rightHandLayer);
        controller.AddLayer(leftHandLayer);

        gestureController.sGestures_L.Clear();
        gestureController.sGestures_R.Clear();
        gestureController.sFaceExp.Clear();

        foreach (var clip in handAnimationClips)
        {
            gestureController.sGestures_L.Add(clip.name);
            gestureController.sGestures_R.Add(clip.name);
        }

        foreach (var clip in faceAnimationClips)
            gestureController.sFaceExp.Add(clip.name);

        EditorUtility.SetDirty(gestureController);

        AssetDatabase.SaveAssets();
        AssetDatabase.Refresh();
    }

    AnimatorControllerLayer CreateLayer(AnimatorController controller, string name, AvatarMask mask, float weight = 1f)
    {
        AnimatorControllerLayer layer = new AnimatorControllerLayer();

        layer.avatarMask = mask;
        layer.name = name;
        layer.stateMachine = new AnimatorStateMachine();
        layer.defaultWeight = weight;
        layer.stateMachine.name = layer.name;
        layer.stateMachine.hideFlags = HideFlags.HideInHierarchy;

        if (AssetDatabase.GetAssetPath(controller) != "")
            AssetDatabase.AddObjectToAsset(layer.stateMachine, AssetDatabase.GetAssetPath(controller));

        return layer;
    }

    private void CreateParameters(AnimatorController controller, List<AnimationClip> animationClips, string prefix = "")
    {
        foreach (var clip in animationClips)
        {
            var newParam = new AnimatorControllerParameter();
            newParam.type = AnimatorControllerParameterType.Bool;
            newParam.defaultBool = false;
            newParam.name = prefix + clip.name;
            controller.AddParameter(prefix + clip.name, AnimatorControllerParameterType.Bool);
        }
    }

    private void AddClipsToState(AnimatorStateMachine stateMachine, List<AnimationClip> animationClips, string prefix = "")
    {
        foreach (var clip in animationClips)
        {
            var state = stateMachine.AddState(clip.name);
            state.motion = clip;
            var transition = stateMachine.AddEntryTransition(state);
            transition.AddCondition(AnimatorConditionMode.If, 1f, prefix + clip.name);
            var exitTransition = state.AddExitTransition();
            exitTransition.hasExitTime = true;
            exitTransition.duration = 0.1f;
        }
    }
}
