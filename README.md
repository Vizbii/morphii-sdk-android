![Morphii SDK](https://d24bblcmguio2o.cloudfront.net/content/images/logo-black-color@3x.png)

# Morphii SDK

## What It Is
The Morphii SDK provides developers with the ability to add a morphii technology to Android native apps. Morphii allows the user to manipulate the type and intensity of the emotion. There are several different morphiis to allow for a full range of possible emotions. The developer also has the option to include a comment section along with the morphii. The developer then can gather pertinent information from the user's interaction with the morphiis to use for analytical purposes. For more information see the [Morphii Site](http://morphii.com).

## How to Use It

### Requirements

- Android Studio 2.+
- Minimum SDK of Project no lower than 15

### Project Setup
The Morphii SDK is located on Bintray: https://bintray.com/vizbii/MorphiiSDKAndroid

- If you are using gradle, add the following line to your app module's gradle file under dependencies:
`compile 'com.morphii.sdk:morphii-sdk-android:1.0.1'`

- If you are using Maven add the following

```xml
<dependency>
  <groupId>com.morphii.sdk</groupId>
  <artifactId>morphii-sdk-android</artifactId>
  <version>1.0.1</version>
  <type>pom</type>
</dependency>
```

- Add `<uses-permission android:name="android.permission.INTERNET"/>` to your `AndroidManifest` file.


### Imports
The following are imports needed to use the Morphii SDK.

```java
import com.morphii.sdk.AuthenticationResults;
import com.morphii.sdk.BasicView;
import com.morphii.sdk.BasicViewConfiguration;
import com.morphii.sdk.Comment;
import com.morphii.sdk.Constants;
import com.morphii.sdk.MorphiiConfiguration;
import com.morphii.sdk.MorphiiSelectionView;
import com.morphii.sdk.MorphiiService;
import com.morphii.sdk.Options;
import com.morphii.sdk.Project;
import com.morphii.sdk.ReactionResultRecord;
import com.morphii.sdk.ReactionService;
import com.morphii.sdk.Target;
import com.morphii.sdk.User;

import static com.morphii.sdk.MorphiiService.sharedInstance;
```

### <a name="morphiiservice"></a>MorphiiService
The MorphiiService class is the main class the developer will work with. This class holds methods for authentication, adding a MorphiiView, and submitting data to receive the Result Records, which contains the data of user inputs. All MorphiiService methods will be called from a sharedInstance object. Example:
`MorphiiService.sharedInstance().setup(yourContext);`

#### Setup
`void setUp(Context context)`

This is the first method that **MUST** be called before any other method. It requires a `context` to be passed in as a parameter.

```java
public class MainActivity extends AppCompatActivity implements BasicView.BasicViewDelegate, MorphiiSelectionView.MorphiiSelectionCallback {
	@Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    sharedInstance().setUp(MainActivity.this);
  }
}
```

#### Authenticate
`void authenticate(String username, String password, String accountId, MorphiiService.AuthenticationCallback callback)`

This method authenticates the developer's account with the morphii servers. It requires a `username`, `password`, `account ID`, and an `Authentication callback`. The callback returns an [AuthenticationResults](#authenticationresults) object. The `authenticate` method must successfully authenticate the user before any other SDK method is used. It is recommended to run a check to ensure the authentication results did not return an `error` object and `isAuthenticated()` returns true. If there is an error the `error` object contains a `code` and `message` to give insight to the developer as to what went wrong. If there is no error and the authentication was successful then it is recommended to add any MorphiiViews inside this callback.

```java
public class MainActivity extends AppCompatActivity implements BasicView.BasicViewDelegate, MorphiiSelectionView.MorphiiSelectionCallback {
	@Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    sharedInstance().setUp(MainActivity.this);
    sharedInstance().authenticate("user_name", "password", "account-id", new MorphiiService.AuthenticationCallback() {
      @Override
      public void authenticated(AuthenticationResults authenticationResults) {
        if (authenticationResults.isAuthenticated()) {
          // Authentication successful.
          // Add morphii views.
        }
        else {
          // Authentication failed.
          Log.d(Constants.TAG, "error code: " + authenticationResults.error().code());
          Log.d(Constants.TAG, "error message: " + authenticationResults.error().message());
        }
      }
    });
  }
}
```

#### Add
`BasicView add(BasicViewConfiguration configuration, Context context, BasicViewDelegate delegate);`

This method adds a [BasicView](#basicview) to the specified container layout. The method requires one parameter: a `basicViewConfiguration`. To create an instance of [BasicViewConfiguration](#basicviewconfiguration) you will need to create a number of supporting classes to pass into the configuration as parameters. Check the Supporting Classes section for more information. The `add` method will return a [BasicView](#basicview) if successful and `null` if there was an error. Keep in mind that the BasicView renders the morphii based off of the container’s width. If the comment field and/or label is desired to be shown then the container layout needs to have a greater height than it does width. Generally adding 20dp to the height for the label and/or comment field should suffice. So if the label and comment field were desired to be shown then adding a total of 40dp would be necessary. The label and comment field will potentially be rendered on top of the morphii if the height is the same as or less than the width.

```java
public BasicView createBasicView() {
  // Project information (required)
  Project project = new Project("my-project-id", "My project description");

  // Target information (required)
  HashMap<String, Object> metaData = new HashMap<>();
  metaData.put("key1", "value-1");
  metaData.put("key2", "value-2");
  Target target = new Target("my-target-id", "question", metaData);

  // Options configuration (required)
  Options options = new Options("live", 0.8);

  // Comment configuration (required)
  Comment comment = new Comment(false, false, 200, "Comment", "Enter comment");

  // User information (optional)
  HashMap<String, Object> userProperties = new HashMap<>();
  userProperties.put("email", "useremail@emailservice.com");
  userProperties.put("age", "24");
  User user = new User("user-id", "external", userProperties);

  // Morphii configuration.
  MorphiiConfiguration morphiiConfig = new MorphiiConfiguration(true);
  morphiiConfig.add("6202184382145363968", null, 0);

  BasicViewConfiguration config = new BasicViewConfiguration(morphiiConfig, target, project, comment, mOptions, mUser);
  BasicView basicView = MorphiiService.sharedInstance().add(config, this, this);

  return basicView;
}
```

#### <a name="addselectionview"></a>AddSelectionView
`MorphiiSelectionView addSelectionView(Context context, MorphiiConfiguration morphiiConfiguration, Double initialIntensity, int height, MorphiiSelectionCallback callback)`

This method returns a [MorphiiSelectionView](#morphiiselectionview) object that can be added to your container layout just like a `SingleMorphiiView`. This method requires five parameters: `context`, `MorphiiConfiguration`, `initialIntensity`, `height`, and `MorphiiSelectionCallback`. The `context` parameter is your app or activity's `context`. The `MorphiiConfiguration` is an instance of the [MorphiiConfiguration](#morphiiconfiguration) object that configures how the morphii will be displayed. The `initialIntensity` is a Double that determines the starting intensity for the rendered morphiis. The `height` parameter is the height of your container layout. Calling `YourContainerLayout.getHeight()` is recommended for this value. The `MorphiiSelectionCallback` returns the `MorphiiConfiguration` of the selected morphii that can be used to create a new `BasicViewConfiguration` that can be used to add a new `BasicView` to allow the user to manipulate and utilize the selected morphii.

```java
public MorphiiSelectionView createSelectionView() {

  // Morphii configuration.
  MorphiiConfiguration morphiiConfig = new MorphiiConfiguration(true);
  morphiiConfig.add("6202184382145363968", null, 0);

  MorphiiSelectionView morphiiSelectionView = MorphiiService.sharedInstance().addSelectionView(MainActivity.this, morphiiConfig2, 0.8, containerLayout.getHeight(), this);

  return basicView;
}
```


#### Submit
`void submit(ReactionRequestCallback callback)`

This method submits data from all `BasicView`s currently in the app, if they have not been submitted already. It has one parameter: `ReactionRequestCallback`. This callback will pass back an `ArrayList<ReactionResultRecord>`. This `ArrayList` contains a [ReactionResultRecord](#reactionresultrecord) for each `BasicView` that was submitted with the method.

```java
public void submit() {
  MorphiiService.sharedInstance().submit(new ReactionService.ReactionRequestCallback() {
    @Override
    public void reactionsSubmitted(ArrayList<ReactionResultRecord> arrayList) {
      for (ReactionResultRecord resultRecord:arrayList) {
        Log.d(Constants.TAG, "is submitted: " + resultRecord.isSubmitted());
        Log.d(Constants.TAG, "view id: " + resultRecord.viewId());
        Log.d(Constants.TAG, "target id: " + resultRecord.targetId());
        if (resultRecord.isSubmitted()) {
          Log.d(Constants.TAG, "reaction id: " + resultRecord.reactionId());
          if (resultRecord.morphii() != null) {
            Log.d(Constants.TAG, "morphii id: " + resultRecord.morphii().id());
            Log.d(Constants.TAG, "morphii name: " + resultRecord.morphii().name());
            Log.d(Constants.TAG, "morphii display name: " + resultRecord.morphii().displayName());
            Log.d(Constants.TAG, "morphii intensity: " + resultRecord.morphii().intensity());
            Log.d(Constants.TAG, "morphii weight: " + resultRecord.morphii().weight());
          }

          if (resultRecord.comment() != null) {
            Log.d(Constants.TAG, "comment text: " + resultRecord.comment().text());
            Log.d(Constants.TAG, "comment locale: " + resultRecord.comment().locale());
          }
          else {
            Log.d(Constants.TAG, "No comment data");
          }
        }
        else {
          Log.d(Constants.TAG, "Submit failed");
          Log.d(Constants.TAG, "error code: " + resultRecord.error().code());
          Log.d(Constants.TAG, "error message: " + resultRecord.error().message());
        }
        Log.d(Constants.TAG, "=================================================");
      }
    }
  });
}
```

#### ResetAll
`void resetAll(boolean comment)`

This method resets all of the `BasicView`s to their default configuration. The method has one parameter: `comment`. This parameter is a boolean that if true will reset the comment field to its default value and if false will retain the user made changes.

```java
public void reset() {
  MorphiiService.sharedInstance().resetAll(true);
}
```


### <a name="basicview"></a>BasicView
These methods are similar to those found within the [MorphiiService](#morphiiservice) class but generally change in regards to the scope of what is affected when called.

#### Submit
`void submit(BasicView basicView, ReactionRequestCallback callback)`

This method takes the data from the specific `BasicView` and submits it. It will return an `ArrayList<ReactionResultRecord>`. The `ArrayList` will have one [ReactionResultRecord](#reactionresultrecord) since only one `BasicView` was submitted.

```java
public void submit() {
  // The mBasicView variable is from creating a BasicView.
  BasicView.submit(mBasicView, new ReactionService.ReactionRequestCallback() {
    @Override
    public void reactionsSubmitted(ArrayList<ReactionResultRecord> arrayList) {
      for (ReactionResultRecord resultRecord:arrayList) {
        Log.d(Constants.TAG, "is submitted: " + resultRecord.isSubmitted());
        Log.d(Constants.TAG, "view id: " + resultRecord.viewId());
        Log.d(Constants.TAG, "target id: " + resultRecord.targetId());
        if (resultRecord.isSubmitted()) {
          Log.d(Constants.TAG, "reaction id: " + resultRecord.reactionId());
          if (resultRecord.morphii() != null) {
            Log.d(Constants.TAG, "morphii id: " + resultRecord.morphii().id());
            Log.d(Constants.TAG, "morphii name: " + resultRecord.morphii().name());
            Log.d(Constants.TAG, "morphii display name: " + resultRecord.morphii().displayName());
            Log.d(Constants.TAG, "morphii intensity: " + resultRecord.morphii().intensity());
            Log.d(Constants.TAG, "morphii weight: " + resultRecord.morphii().weight());
          }

          if (resultRecord.comment() != null) {
            Log.d(Constants.TAG, "comment text: " + resultRecord.comment().text());
            Log.d(Constants.TAG, "comment locale: " + resultRecord.comment().locale());
          }
          else {
            Log.d(Constants.TAG, "No comment data");
          }
        }
        else {
          Log.d(Constants.TAG, "Submit failed");
          Log.d(Constants.TAG, "error code: " + resultRecord.error().code());
          Log.d(Constants.TAG, "error message: " + resultRecord.error().message());
        }
        Log.d(Constants.TAG, "=================================================");
      }
    }
  });
}
```

#### <a name="png"></a>PNG
`Bitmap png()`

This method returns a `bitmap` of the morphii in a specified `BasicView`. The view must be submitted before this method will return the Bitmap data.

```java
public void getImage() {
  // The mBasicView variable is from creating a BasicView.
  Bitmap bitmap = mBasicView.png();
  if (bitmap != null) {
    final ImageView imageView = (ImageView)findViewById(R.id.imageView);
    imageView.setImageBitmap(bitmap);
  }
  else {
    Log.d(Constants.TAG, "Image is null. Have you submitted the data?");
  }
}
```


#### <a name="reset"></a>Reset
`void reset(boolean comment)`

This method will reset the `BasicView` back to its default values. The method requires one parameter: `comment`. This parameter is a boolean that when marked true will reset the comment field to its default value and if false will retain the user made changes.

```java
public void reset() {
  // The mBasicView variable is from creating a BasicView.
  mBasicView.reset(true);
}
```

### <a name="morphiiselectionview"></a>MorphiiSelectionView
The MorphiiSelectionView is a view that contains a list of morphiis. This object is returned by [AddSelectionView](#addselectionview)


### Supporting Classes
These are the supporting classes as parameters in [MorphiiService](#morphiiservice), [MorphiiSelectionView](#morphiiselectionview), and [BasicView](#basicview) methods.

#### <a name="project"></a>Project
This class is used to group different project data. It requires two parameters: `id` and `description`. The `id` is a String that is used to label the project. The `description` is a String that briefly describes the project. This object is used to construct the [BasicViewConfiguration](#basicviewconfiguration).

```java
// Project information (required)
Project project = new Project("my-project-id", "My project description");
```

#### <a name="target"></a>Target
The `Target` class is utilized to describe what the morphii will be in reference to. The class requires three parameters: `id`, `type`, and `metadata`. The `id` is a String that serves as a label for the referenced object. The `type` is a String that describes what the `Target` is. The `type` should be a value like: question, image, video, article, etc. The `metadata` object is a dictionary of additional information the developer would like to maintain a record of; this parameter can be `null`. This object is used to construct the [BasicViewConfiguration](#basicviewconfiguration).

```java
// Target information
HashMap<String, Object> metaData = new HashMap<>();
metaData.put("question", "How does this image make you feel?");
metaData.put("url", "https://image.com/image.png");
metaData.put("key-1", "value-1");
Target target = new Target("question-1", "image", metaData);
```

#### <a name="user"></a>User
This class is used to define the user that is interacting with the morphii. It uses three parameters: `id`, `type`, and `properties`. The `id` is a String that serves as the id for the user. The `type` is a String that defines the type of user. Choices for the `type` include: external, facebook, twitter, or google. The `properties` parameter is a dictionary that the developer may use to collect any additional information they desire; this parameter may be `null`. This object is used to construct the [BasicViewConfiguration](#basicviewconfiguration).

```java
// User information
HashMap<String, Object> userProperties = new HashMap<>();
userProperties.put("email", "useremail@emailservice.com");
userProperties.put("age", "24");
User user = new User("user-id", "external", userProperties);
```

#### <a name="comment"></a>Comment
This class is used to configure the comment section of a [BasicView](#basicview). The class requires five parameters: `show`, `required`, `maxLength`, `label`, and `hintText`. The `show` parameter is a boolean that determines whether the comment section will be visible or not. The `required` parameter is a boolean that determines whether the user is required to fill in a comment in order to submit the BasicView. The `maxLength` parameter is an int that determines the maximum amount of characters allowed in a comment; if set to 0 an unlimited amount of characters are allowed. The `label` parameter is a String that appears above the comment field. The `hintText` is a String that will display within the comment field. This object is used to construct the [BasicViewConfiguration](#basicviewconfiguration).

```java
// Comment configuration
Comment comment = new Comment(false, false, 200, "Comment", "Enter comment");
```

#### <a name="options"></a>Options
This class is used to define additional configuration. It requires two parameters: `stage` and `initialIntensity`. The `stage` parameter is a String that has three options: `test`, `validation`, and `live`. The `test` option will have no data processed by the data analytics pipeline. The `validation` option will send data to be processed by the analytics pipeline but will not show up in any report data. The `live` option will have data be processed through all stages of the data analytics pipeline and report data. The `initialIntensity` parameter is a double that determines the starting intensity of the morphii. This value is required to be between 0 and 1. This object is used to construct the [BasicViewConfiguration](#basicviewconfiguration).

```java
// Options configuration
Options options = new Options("live", 0.8);
```

#### <a name="morphiiconfiguration"></a>MorphiiConfiguration
This class is used to define the default configuration of the morphii. The `MorphiiConfiguration` object takes one parameter: `showName`. This parameter is a boolean that when true, will display the name below the morphii. When false the name will not be shown. After creating a `MorphiiConfiguration` object it is necessary to call its `add` method in order to add information to the configuration. The method requires three parameters: `id`, `name`, `weight`. The `id` is a String that determines which morphii will be added. The `name` is a developer defined label for the morphii. This parameter can be defined as `null`. If `null` the default morphii name will be used. The `weight` parameter is an int that is used to assign a weight to the morphii. This object is used to construct the [BasicViewConfiguration](#basicviewconfiguration) and add a [MorphiiSelectionView](#morphiiselectionview).

##### Methods
- `void add(String id, String name, int weight)` : This method adds a specific morphii to the MorphiiConfiguration.

```java
// Morphii configuration.
MorphiiConfiguration morphiiConfig = new MorphiiConfiguration(true);
morphiiConfig.add("6202184382145363968", null, 0);
```

#### <a name="basicviewconfiguration"></a>BasicViewConfiguration
This class defines the [BasicView](#basicview) configuration. It is used to create a new `BasicView`. The object requires six parameters: `morphiiConfiguration`, `target`, `project`, `comment`, `options`, `user`. Reference above for aid in creating these objects.

```java
// Project information (required)
Project project = new Project("my-project-id", "My project description");

// Target information (required)
HashMap<String, Object> metaData = new HashMap<>();
metaData.put("key1", "value-1");
metaData.put("key2", "value-2");
Target target = new Target("my-target-id", "question", metaData);

// Options configuration (required)
Options options = new Options("live", 0.8);

// Comment configuration (required)
Comment comment = new Comment(false, false, 200, "Comment", "Enter comment");

// User information (optional)
HashMap<String, Object> userProperties = new HashMap<>();
userProperties.put("email", "useremail@emailservice.com");
userProperties.put("age", "24");
User user = new User("user-id", "external", userProperties);

// Morphii configuration.
MorphiiConfiguration morphiiConfig = new MorphiiConfiguration(true);
morphiiConfig.add("6202184382145363968", null, 0);

BasicViewConfiguration config = new BasicViewConfiguration(morphiiConfig, target, project, comment, mOptions, mUser);
```

#### <a name="reactionresultrecord"></a>ReactionResultRecord
The ReactionResultRecord object is returned from the `submit` methods. Note, if account subscription expires or record limit is met the ReactionResultRecord object will not return the details in the [ReactionMorphiiResultRecord](#reactionmorphiiresultrecord) and [ReactionCommentRecord](#reactioncommentrecord) objects.

##### Methods
- `boolean isSubmitted()` : This method returns if the reaction was successfully submitted.
- `String viewId()` : This method returns the view id.
- `String targetId()` : This method returns the target id associated with this reaction.
- `String reactionId()` : This method returns the reaction id generated for this reaction.
- `ReactionMorphiiResultRecord morphii()` : The method returns an [ReactionMorphiiResultRecord](#reactionmorphiiresultrecord) object which contains more details about the reaction morphii.
- `ReactionCommentRecord comment()` : The method returns an [ReactionCommentRecord](#reactioncommentrecord) object which contains more details about the reaction comment.
- `ReactionError error()` : The method returns an [ReactionError](#reactionerror) object which contains more details about the reaction error.

```java
public void submit() {
  MorphiiService.sharedInstance().submit(new ReactionService.ReactionRequestCallback() {
    @Override
    public void reactionsSubmitted(ArrayList<ReactionResultRecord> arrayList) {
      for (ReactionResultRecord resultRecord:arrayList) {
        Log.d(Constants.TAG, "is submitted: " + resultRecord.isSubmitted());
        Log.d(Constants.TAG, "view id: " + resultRecord.viewId());
        Log.d(Constants.TAG, "target id: " + resultRecord.targetId());
        if (resultRecord.isSubmitted()) {
          Log.d(Constants.TAG, "reaction id: " + resultRecord.reactionId());
          if (resultRecord.morphii() != null) {
            Log.d(Constants.TAG, "morphii id: " + resultRecord.morphii().id());
            Log.d(Constants.TAG, "morphii name: " + resultRecord.morphii().name());
            Log.d(Constants.TAG, "morphii display name: " + resultRecord.morphii().displayName());
            Log.d(Constants.TAG, "morphii intensity: " + resultRecord.morphii().intensity());
            Log.d(Constants.TAG, "morphii weight: " + resultRecord.morphii().weight());
          }

          if (resultRecord.comment() != null) {
            Log.d(Constants.TAG, "comment text: " + resultRecord.comment().text());
            Log.d(Constants.TAG, "comment locale: " + resultRecord.comment().locale());
          }
          else {
            Log.d(Constants.TAG, "No comment data");
          }
        }
        else {
          Log.d(Constants.TAG, "Submit failed");
          Log.d(Constants.TAG, "error code: " + resultRecord.error().code());
          Log.d(Constants.TAG, "error message: " + resultRecord.error().message());
        }
        Log.d(Constants.TAG, "=================================================");
      }
    }
  });
}
```

#### <a name="reactionmorphiiresultrecord"></a>ReactionMorphiiResultRecord
This object contains detail information for the morphii associated with the [ReactionResultRecord](#reactionresultrecord).

##### Methods
- `String id()` : This method returns the unique morphii id associated with this reaction.
- `String name()` : This method returns the morphii name associated with this reaction.
- `String displayName()` : This method returns the morphii display name associated with this reaction.
- `Double intensity()` : This method returns the morphii intensity associated with this reaction.
- `double weight()` : This method returns the morphii weight associated with this reaction.

#### <a name="reactioncommentrecord"></a>ReactionCommentRecord
This object contains detail information for the user entered comment associated with the [ReactionResultRecord](#reactionresultrecord).

##### Methods
- `String text()` : This method returns the comment text associated with this reaction.
- `String locale()` : This method returns the comment locale associated with this reaction.

#### <a name="reactionerror"></a>ReactionError
This object gives further details into any errors that may have occurred during the submission process. It contains two properties: `code` and `message`. The `code` property is a specific error code defined by the data analytics pipeline. The `message` property is a message associated with the `code` property that explains what caused the error.

##### Methods
- `String code()` : This method returns reaction error code string.
- `String message()` : This method returns reaction error message string.


#### <a name="authenticationresults"></a>AuthenticationResults
This object gives further details the authentication results. This object is returned via the authentication callback.

##### Methods
- `boolean isAuthenticated()` : This method returns if the authentication was successful.
- `AuthenticationError error()` : The method returns an [AuthenticationError](#authenticationerror) object which contains more details about the authentication error.


#### <a name="authenticationerror"></a>AuthenticationError
This object gives further details into any errors that may have occurred during the authentication process. It contains two properties: `code` and `message`. The `code` property is a specific error code defined by the data analytics pipeline. The `message` property is a message associated with the `code` property that explains what caused the error.

##### Methods
- `String code()` : This method returns authentication error code string.
- `String message()` : This method returns authentication error message string.
