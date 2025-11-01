/**
 * GamePanel
 * 
 * This panel handles the main game play for the Soccer Penalties Game.
 * It includes logic for shooting the ball, animating the goalie movement,
 * tracking the score, and determining the outcome of each shot.
 * It also resets the game for replay and transitions to the GameOver panel.
 * 
 */
package penaltiesgame;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.Timer;

/**
 *
 * @author Siddhi
 */
public class GamePanel extends javax.swing.JPanel {

    PenaltiesGame mainFrame; // Reference to the main frame to allow panel switching 
    /* Game state variables */
    int totalShots = 0; 
    int maxShots = 5;
    int goalsScored = 0;
    JLabel[] shotLabels; //for displaying scores in squares
    
    /* For animations and randomness */
    Random rand = new Random();
    Timer goalieTimer;
    int goalieDirection = 1; // 1 = right, -1 = left
    
    /* Initial positions for resetting */
    int startBallX;
    int startBallY;
    int startGoalieX;
    int startGoalieY;
    
    /**
     * Shooting
     * Handles animation of the ball toward a target position,
     * checks for collision with the goalie, and updates game results.
     *
     * @param direction Zone name that the user aimed at
     * @param targetX   X coordinate of where the ball should go
     * @param targetY   Y coordinate of where the ball should go
     */
    
    public void Shooting(String direction, int targetX, int targetY){
        if(totalShots >= maxShots){ //if 5 shots are already taken
            return;
        }
        totalShots ++; //updates shot count
        
        /* For the ball animation */
        Timer ballTimer = new Timer(15, null);
        Point start = BallImage.getLocation(); //gets the ball's location
        int startX = start.x;
        int startY = start.y;
        
        ballTimer.addActionListener(new ActionListener() {
        int step = 0;
        public void actionPerformed(ActionEvent e) {
            step++;
            // Simple linear interpolation for movement (diagonal or straight depending on which button is chosen) 
            int newX = startX + (targetX - startX) * step / 20;
            int newY = startY + (targetY - startY) * step / 20;

            BallImage.setLocation(newX, newY); //sets the new location for the ball
            
            // Once ball reaches target, stop (after 20 steps)
            if (step >= 20) {
                ballTimer.stop(); //stops ball
                goalieTimer.stop(); //stops goalie
                /*Gets the boundaries of the ball and goalie*/
                Rectangle ballBounds = BallImage.getBounds();
                Rectangle goalieBounds = GoalieImage.getBounds();
                
                String result; //for the result (goal, miss, save)
                
                /* Collision detection */
                if (ballBounds.intersects(goalieBounds)) { //ball ball and goalie collide
                    result = "SAVE";
                } 
                else if (rand.nextInt(10) < 2) { // random 20% chance of miss
                    result = "MISS";
                } 
                else { //no collision or miss
                    result = "GOAL";
                    goalsScored++; //updates goal count
                }
                

            /*Updates shot tracker*/
            if (totalShots <= shotLabels.length) { //if less than 5 shots are taken
            JLabel currentLabel = shotLabels[totalShots - 1]; //updates the correct label

            switch (result) {
                case "GOAL": //if it's a goal
                    currentLabel.setBackground(Color.GREEN); //changes to green
                    break;
                case "SAVE": //if it's a save
                    currentLabel.setBackground(Color.RED); //changes to red
                    break;
                case "MISS": //if it's a miss
                    currentLabel.setBackground(Color.GRAY); //changes to gray
                    break;
                }
            } 
            
            JOptionPane.showMessageDialog(GamePanel.this, "You aimed at " + direction + ". Result: " + result); //displays the result as well (goal, save, or miss) and the button that was aimed at
            
            goalieTimer.start(); //starts the goalie movement again

            if (totalShots >= maxShots) { //if 5 shots are taken
                goalieTimer.stop(); //stops goalie
                /* Goes to Game Over screen */
                mainFrame.GameOverPanel.setFinalScore(goalsScored); //saves the score in a method in the game over panel
                mainFrame.GameOverPanel.resetSaveButton(); //resets the save button on the gave over panel (to save the score multiple times)
                mainFrame.setContentPane(mainFrame.GameOverPanel); //changes to the game over panel
                mainFrame.revalidate();
                mainFrame.repaint();
            } 
 
            BallImage.setLocation(startX, startY); // Resets ball back to starting position
            }
        }
        });
        ballTimer.start(); //starts the ball animation   
    }
    
    
    public void resetGame() {
        /*Resets variables*/
        totalShots = 0;
        goalsScored = 0;

        /* Resets all 5 shot labels to default (gray) */
        for (int i = 0; i < shotLabels.length; i++) {
            shotLabels[i].setBackground(Color.LIGHT_GRAY);
        }

        /* Reset ball and goalie to original positions */
        BallImage.setLocation(startBallX, startBallY);
        GoalieImage.setLocation(startGoalieX, startGoalieY);

        /* Restarts the goalie animation */
        if (goalieTimer != null && !goalieTimer.isRunning()) {
            goalieTimer.start();
        }
    }
    
     
    /**
     * Creates new form GamePanel
     */
    public GamePanel() {
        initComponents();
    }

    GamePanel(PenaltiesGame frame) { 
       initComponents();
        this.mainFrame = frame; //connects the panel to the main frame
        shotLabels = new JLabel[] { lblShot1, lblShot2, lblShot3, lblShot4, lblShot5 }; //creates the labels to display the score
    
        /* Stores initial positions */
        Point ballStart = BallImage.getLocation(); //ball positions
        startBallX = ballStart.x;
        startBallY = ballStart.y;

        Point goalieStart = GoalieImage.getLocation(); //goalie positions
        startGoalieX = goalieStart.x;
        startGoalieY = goalieStart.y;
        
        
        /* Starts goalie animation */
        goalieTimer = new Timer(1, new ActionListener() { //sets timer
            public void actionPerformed(ActionEvent e) {
                Point pos = GoalieImage.getLocation(); //gets original position
                int newX = pos.x + goalieDirection * 5; //changes position

                /* Reverses direction when hitting boundaries */
                if (newX <= 50 || newX + GoalieImage.getWidth() >= getWidth() - 50) {
                    goalieDirection *= -1;
                }

                GoalieImage.setLocation(newX, pos.y); //sets new position forc each time interval
            }
        });
        goalieTimer.start(); //starts goalie
        
        btnBottomRow.addMouseListener(new MouseAdapter() { //for the bottom row button
    @Override
    public void mouseClicked(MouseEvent e) {
        goalieTimer.stop(); //stops goalie
        /* Gets the X position where the user clicked on the button */
        int clickX = e.getX(); // relative to the button
        int buttonX = btnBottomRow.getX(); // position of button on panel
        int targetX = buttonX + clickX; // actual X on the panel
        int targetY = btnBottomRow.getY(); // Y of the bottom row
        Shooting("BottomRow", targetX, targetY); //applies method to the button
        goalieTimer.start(); //starts goalie again
        }
    }); 
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblInstructions = new javax.swing.JLabel();
        lblInstructions2 = new javax.swing.JLabel();
        lblScores = new javax.swing.JLabel();
        lblShot1 = new javax.swing.JLabel();
        lblShot2 = new javax.swing.JLabel();
        lblShot3 = new javax.swing.JLabel();
        lblShot4 = new javax.swing.JLabel();
        lblShot5 = new javax.swing.JLabel();
        btnHome = new javax.swing.JButton();
        BallImage = new javax.swing.JLabel();
        GoalieImage = new javax.swing.JLabel();
        btnBottomRow = new javax.swing.JButton();
        btnTopCentre = new javax.swing.JButton();
        btnMiddleCentre = new javax.swing.JButton();
        btnMiddleLeft = new javax.swing.JButton();
        btnMiddleRight = new javax.swing.JButton();
        btnTopLeft = new javax.swing.JButton();
        btnTopRight = new javax.swing.JButton();
        BackgroundImage = new javax.swing.JLabel();

        setLayout(null);

        lblInstructions.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblInstructions.setForeground(new java.awt.Color(255, 255, 255));
        lblInstructions.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblInstructions.setText("Click anywhere on the net to shoot. ");
        add(lblInstructions);
        lblInstructions.setBounds(290, 20, 260, 33);

        lblInstructions2.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        lblInstructions2.setForeground(new java.awt.Color(255, 255, 255));
        lblInstructions2.setText("(Misses are random)");
        add(lblInstructions2);
        lblInstructions2.setBounds(350, 60, 130, 16);

        lblScores.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        lblScores.setForeground(new java.awt.Color(255, 255, 255));
        lblScores.setText("Score:");
        add(lblScores);
        lblScores.setBounds(50, 30, 47, 21);

        lblShot1.setBackground(new java.awt.Color(204, 204, 204));
        lblShot1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(242, 246, 255), null, null, new java.awt.Color(250, 250, 250)));
        lblShot1.setOpaque(true);
        add(lblShot1);
        lblShot1.setBounds(110, 30, 20, 20);

        lblShot2.setBackground(new java.awt.Color(204, 204, 204));
        lblShot2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(242, 246, 255), null, null, new java.awt.Color(250, 250, 250)));
        lblShot2.setOpaque(true);
        add(lblShot2);
        lblShot2.setBounds(140, 30, 20, 20);

        lblShot3.setBackground(new java.awt.Color(204, 204, 204));
        lblShot3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(242, 246, 255), null, null, new java.awt.Color(250, 250, 250)));
        lblShot3.setOpaque(true);
        add(lblShot3);
        lblShot3.setBounds(170, 30, 20, 20);

        lblShot4.setBackground(new java.awt.Color(204, 204, 204));
        lblShot4.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(242, 246, 255), null, null, new java.awt.Color(250, 250, 250)));
        lblShot4.setOpaque(true);
        add(lblShot4);
        lblShot4.setBounds(200, 30, 20, 20);

        lblShot5.setBackground(new java.awt.Color(204, 204, 204));
        lblShot5.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(242, 246, 255), null, null, new java.awt.Color(250, 250, 250)));
        lblShot5.setOpaque(true);
        add(lblShot5);
        lblShot5.setBounds(230, 30, 20, 20);

        btnHome.setBackground(new java.awt.Color(254, 211, 167));
        btnHome.setText("Back to Home");
        btnHome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHomeActionPerformed(evt);
            }
        });
        add(btnHome);
        btnHome.setBounds(597, 20, 150, 30);

        BallImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/penaltiesgame/images/soccer ball.png"))); // NOI18N
        BallImage.setFocusTraversalPolicyProvider(true);
        add(BallImage);
        BallImage.setBounds(330, 480, 100, 100);

        GoalieImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/penaltiesgame/images/goalie.png"))); // NOI18N
        add(GoalieImage);
        GoalieImage.setBounds(280, 170, 231, 300);

        btnBottomRow.setBorderPainted(false);
        btnBottomRow.setContentAreaFilled(false);
        btnBottomRow.setName("BottomRow"); // NOI18N
        add(btnBottomRow);
        btnBottomRow.setBounds(80, 360, 620, 80);

        btnTopCentre.setBorderPainted(false);
        btnTopCentre.setContentAreaFilled(false);
        btnTopCentre.setName("TopCentre"); // NOI18N
        btnTopCentre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTopCentreActionPerformed(evt);
            }
        });
        add(btnTopCentre);
        btnTopCentre.setBounds(270, 160, 210, 110);

        btnMiddleCentre.setBorderPainted(false);
        btnMiddleCentre.setContentAreaFilled(false);
        btnMiddleCentre.setName("MiddleCentre"); // NOI18N
        btnMiddleCentre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMiddleCentreActionPerformed(evt);
            }
        });
        add(btnMiddleCentre);
        btnMiddleCentre.setBounds(270, 260, 220, 110);

        btnMiddleLeft.setBorderPainted(false);
        btnMiddleLeft.setContentAreaFilled(false);
        btnMiddleLeft.setName("MiddleLeft"); // NOI18N
        btnMiddleLeft.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMiddleLeftActionPerformed(evt);
            }
        });
        add(btnMiddleLeft);
        btnMiddleLeft.setBounds(70, 260, 210, 110);

        btnMiddleRight.setBorderPainted(false);
        btnMiddleRight.setContentAreaFilled(false);
        btnMiddleRight.setName("MiddleRight"); // NOI18N
        btnMiddleRight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMiddleRightActionPerformed(evt);
            }
        });
        add(btnMiddleRight);
        btnMiddleRight.setBounds(480, 260, 210, 110);

        btnTopLeft.setBackground(new java.awt.Color(146, 57, 57));
        btnTopLeft.setBorderPainted(false);
        btnTopLeft.setContentAreaFilled(false);
        btnTopLeft.setName("TopLeft"); // NOI18N
        btnTopLeft.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTopLeftActionPerformed(evt);
            }
        });
        add(btnTopLeft);
        btnTopLeft.setBounds(70, 160, 210, 110);

        btnTopRight.setBorderPainted(false);
        btnTopRight.setContentAreaFilled(false);
        btnTopRight.setName("TopRight"); // NOI18N
        btnTopRight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTopRightActionPerformed(evt);
            }
        });
        add(btnTopRight);
        btnTopRight.setBounds(470, 160, 210, 110);

        BackgroundImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/penaltiesgame/images/GoalBackground.jpg"))); // NOI18N
        add(BackgroundImage);
        BackgroundImage.setBounds(0, -10, 780, 610);
    }// </editor-fold>//GEN-END:initComponents

    private void btnHomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHomeActionPerformed
        /*Changes to home panel*/
        mainFrame.setContentPane(mainFrame.HomePanel);
        mainFrame.revalidate();
        mainFrame.repaint();
    }//GEN-LAST:event_btnHomeActionPerformed

    private void btnTopLeftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTopLeftActionPerformed
        Shooting("TopLeft", 100,140); //calls the shooting method for the button
    }//GEN-LAST:event_btnTopLeftActionPerformed

    private void btnTopCentreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTopCentreActionPerformed
       Shooting("TopCentre", 315, 140); //calls the shooting method for the button
    }//GEN-LAST:event_btnTopCentreActionPerformed

    private void btnTopRightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTopRightActionPerformed
        Shooting("TopRight", 600, 140); //calls the shooting method for the button 
    }//GEN-LAST:event_btnTopRightActionPerformed

    private void btnMiddleLeftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMiddleLeftActionPerformed
        Shooting("MiddleLeft", 100, 235); //calls the shooting method for the button
    }//GEN-LAST:event_btnMiddleLeftActionPerformed

    private void btnMiddleCentreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMiddleCentreActionPerformed
        Shooting("MiddleCentre", 315, 235); //calls the shooting method for the button
    }//GEN-LAST:event_btnMiddleCentreActionPerformed

    private void btnMiddleRightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMiddleRightActionPerformed
        Shooting("MiddleRight", 550, 235); //calls the shooting method for the button
    }//GEN-LAST:event_btnMiddleRightActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel BackgroundImage;
    private javax.swing.JLabel BallImage;
    private javax.swing.JLabel GoalieImage;
    private javax.swing.JButton btnBottomRow;
    private javax.swing.JButton btnHome;
    private javax.swing.JButton btnMiddleCentre;
    private javax.swing.JButton btnMiddleLeft;
    private javax.swing.JButton btnMiddleRight;
    private javax.swing.JButton btnTopCentre;
    private javax.swing.JButton btnTopLeft;
    private javax.swing.JButton btnTopRight;
    private javax.swing.JLabel lblInstructions;
    private javax.swing.JLabel lblInstructions2;
    private javax.swing.JLabel lblScores;
    private javax.swing.JLabel lblShot1;
    private javax.swing.JLabel lblShot2;
    private javax.swing.JLabel lblShot3;
    private javax.swing.JLabel lblShot4;
    private javax.swing.JLabel lblShot5;
    // End of variables declaration//GEN-END:variables
}
