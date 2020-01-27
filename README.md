# Drone Simulator GUI.
This is my attempt at a GUI for a my Drone Simulator. Starting again in regards to design but using old Drone Simulator project as reference.

After some research, I've decided to create my Drone Simulation with the inspiration of boids. I will be implementing three rules to emulate 'flocking' which is a behaviour of which boids are famous for. So far, I've discovered three rules: Cohesion, Alignment, Separation - I'm hoping these get me closer to my goal of a drone simulator. 

I'm hoping to eventually progress and create 'Predator drones' which will be bigger and will cause normal drones to 'scatter' when encountering this particular type of drone.


## Checklist
- [x] Drones adhere to Cohesion Rule function
- [x] Drones adhere to Separation Rule function
- [x] Drones adhere to Alignment Rule function
- [x] Drones adhere to Avoidance Rule function
- [x] Drones adhere to Scatter Rule function

These 'rule' functions output varied vector values in regards to the next location for each of the drones. However, I'm finding that some values are 'stronger' than those outputted by other values. This means that when I use my sliders in my GUI window, the outputted values from each rule function are multiplied by the value the slider is set at. Some of these sliders multiply the values of say the 'alignment' vector value by tooo much and therefore the movement pattern for that particular function is exaggerated with minimal slider movement... 

<details><summary>Trial 1</summary>
	
![ezgif com-video-to-gif](https://user-images.githubusercontent.com/45906280/72012793-746a8100-3254-11ea-8f74-5337587502a2.gif)
</details>

<details><summary>Trial 2</summary>
	
![ezgif com-video-to-gif](https://github.com/zardoss/GUI---Drone-Simulator/blob/master/videos/Trial%202.gif)

Here, I believe the cohesion and separation functions were working correctly however as I started to work on different aspects of the program, these simple functions became complicated...
</details>

<details><summary>Trial 3</summary>
	
![ezgif com-video-to-gif](https://github.com/zardoss/GUI---Drone-Simulator/blob/master/videos/Trial%203.gif)

Here's an example of how I unintentionally reversed progress and broke my program. The drones are all moving to the bottom corner here. I believe the reason could be that one of the rule functions determining next movement were outputting a super high value. Due to window boundaries I'd set, the drones could not move 'out of scope'.
</details>

### Trial 4
### Trial 5
### Trial 6
### Trial 7
### Successful Trial...