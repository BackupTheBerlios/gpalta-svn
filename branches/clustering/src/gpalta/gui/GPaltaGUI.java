/*
 * GPaltaGUI.java
 *
 * Created on 25 de mayo de 2005, 07:14 PM
 *
 * Copyright (C) 2005, 2006 Neven Boric <nboric@gmail.com>
 *
 * This file is part of GPalta.
 *
 * GPalta is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * GPalta is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GPalta; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package gpalta.gui;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.util.*;

import ptolemy.plot.Plot;
import gpalta.core.*;

/**
 *
 * @author  neven
 */
public class GPaltaGUI extends javax.swing.JFrame {
    
    public EvolutionThread evoThread;
    private List<javax.swing.JComponent> disableWhenRunning;
    private List<javax.swing.JComponent> disableAtStart;
    private List<javax.swing.JComponent> disableWhenNotRunning;
    public boolean stopAtNextGen;
    private boolean first;
    private int nGenDone;
    private Plot plot;
    private boolean usePlot;
    public boolean stopSaveQuit;
    private Timer timer;
    public Config config;
    
    
    /**
     * Creates new form GPaltaGUI 
     */
    public GPaltaGUI()
    {
        //First thing to do is read config from file
        config = new Config("Config.txt");
        
        initComponents();
        
        disableAtStart = new ArrayList<javax.swing.JComponent>();
        //These components will be disabled when the app starts
        disableAtStart.add(checkSave);
        disableAtStart.add(spinSaveInterval);
        disableAtStart.add(butGo1);
        disableAtStart.add(butGoN);
        disableAtStart.add(spinGoN);
        disableAtStart.add(togButStopAtNextGen);
        disableAtStart.add(butSaveNow);
        disableAtStart.add(labelGen);
        disableAtStart.add(togStopSaveQuit);
        setEnabledAll(disableAtStart, false);
        
        disableWhenRunning = new ArrayList<javax.swing.JComponent>();
        //These components will be disabled when the evolution is running
        disableWhenRunning.add(butSaveNow);
        disableWhenRunning.add(spinGoN);
        disableWhenRunning.add(butGoN);
        disableWhenRunning.add(butGo1);
        disableWhenRunning.add(butNewFromFile);
        disableWhenRunning.add(butNewFromScratch);
        disableWhenRunning.add(checkSave);
        disableWhenRunning.add(spinSaveInterval);
        disableWhenRunning.add(labelGen);
        
        disableWhenNotRunning = new ArrayList<javax.swing.JComponent>();
        //These components will be disabled when the evolution is stopped
        disableWhenNotRunning.add(togButStopAtNextGen);
        setEnabledAll(disableWhenNotRunning, false);
        
        stopAtNextGen = false;
        stopSaveQuit = false;
        
        usePlot = true;
        
        if (config.nonInteractive)
        {
            newEvo(true);
            checkSave.setSelected(true);
            togStopSaveQuit.doClick();
            butGoN.doClick();
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jScrollBar1 = new javax.swing.JScrollBar();
        panelTop = new javax.swing.JPanel();
        panelEvolution = new javax.swing.JPanel();
        butNewFromScratch = new javax.swing.JButton();
        butNewFromFile = new javax.swing.JButton();
        panelSaving = new javax.swing.JPanel();
        butSaveNow = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        checkSave = new javax.swing.JCheckBox();
        spinSaveInterval = new javax.swing.JSpinner();
        labelGen = new javax.swing.JLabel();
        panelGo = new javax.swing.JPanel();
        butGo1 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        spinGoN = new javax.swing.JSpinner();
        butGoN = new javax.swing.JButton();
        panelStop = new javax.swing.JPanel();
        togButStopAtNextGen = new javax.swing.JToggleButton();
        togStopSaveQuit = new javax.swing.JToggleButton();
        panelStats = new javax.swing.JPanel();
        panelBestSoFar = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        textBestSoFar = new javax.swing.JTextArea();
        butCopy = new javax.swing.JButton();
        panelCentralStats = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        panelStatsNames = new javax.swing.JPanel();
        labelGenName = new javax.swing.JLabel();
        labelBestFitThisName = new javax.swing.JLabel();
        labelAvgFitName = new javax.swing.JLabel();
        labelAvgNodesName = new javax.swing.JLabel();
        labelFake3 = new javax.swing.JLabel();
        labelBestSoFar = new javax.swing.JLabel();
        labelBestSoFarFitName = new javax.swing.JLabel();
        labelBestSoFarHR0Name = new javax.swing.JLabel();
        labelBestSoFarHR1Name = new javax.swing.JLabel();
        labelBestSoFarNodesName = new javax.swing.JLabel();
        panelStatsValues = new javax.swing.JPanel();
        labelGenValue = new javax.swing.JLabel();
        labelBestFitThisValue = new javax.swing.JLabel();
        labelAvgFitValue = new javax.swing.JLabel();
        labelAvgNodesValue = new javax.swing.JLabel();
        labelFake4 = new javax.swing.JLabel();
        labelFake5 = new javax.swing.JLabel();
        labelBestSoFarFitValue = new javax.swing.JLabel();
        labelBestSoFarHR0Value = new javax.swing.JLabel();
        labelBestSoFarHR1Value = new javax.swing.JLabel();
        labelBestSoFarNodesValue = new javax.swing.JLabel();
        panelPlot = new javax.swing.JPanel();
        panelBottom = new javax.swing.JPanel();
        labInfo = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        labelProgress = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        jPanel3 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("GPalta");
        panelTop.setLayout(new javax.swing.BoxLayout(panelTop, javax.swing.BoxLayout.X_AXIS));

        panelEvolution.setLayout(new java.awt.BorderLayout(0, 3));

        panelEvolution.setBorder(javax.swing.BorderFactory.createTitledBorder("New Evolution"));
        butNewFromScratch.setText("Create new");
        butNewFromScratch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butNewFromScratchActionPerformed(evt);
            }
        });

        panelEvolution.add(butNewFromScratch, java.awt.BorderLayout.NORTH);

        butNewFromFile.setText("Continue from file");
        butNewFromFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butNewFromFileActionPerformed(evt);
            }
        });

        panelEvolution.add(butNewFromFile, java.awt.BorderLayout.SOUTH);

        panelTop.add(panelEvolution);

        panelSaving.setLayout(new java.awt.BorderLayout(0, 3));

        panelSaving.setBorder(javax.swing.BorderFactory.createTitledBorder("Saving"));
        butSaveNow.setText("Save now");
        butSaveNow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSaveNowActionPerformed(evt);
            }
        });

        panelSaving.add(butSaveNow, java.awt.BorderLayout.NORTH);

        jPanel5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 0));

        checkSave.setText("Save every");
        jPanel5.add(checkSave);

        spinSaveInterval.setValue(100     );
        jPanel5.add(spinSaveInterval);

        labelGen.setText(" generations");
        jPanel5.add(labelGen);

        panelSaving.add(jPanel5, java.awt.BorderLayout.SOUTH);

        panelTop.add(panelSaving);

        panelGo.setLayout(new java.awt.BorderLayout(0, 3));

        panelGo.setBorder(javax.swing.BorderFactory.createTitledBorder("Go"));
        butGo1.setText("Go 1");
        butGo1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butGo1ActionPerformed(evt);
            }
        });

        panelGo.add(butGo1, java.awt.BorderLayout.NORTH);

        jPanel4.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 0));

        spinGoN.setValue(config.nGenerations);
        jPanel4.add(spinGoN);

        butGoN.setText("Go N");
        butGoN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butGoNActionPerformed(evt);
            }
        });

        jPanel4.add(butGoN);

        panelGo.add(jPanel4, java.awt.BorderLayout.SOUTH);

        panelTop.add(panelGo);

        panelStop.setLayout(new java.awt.BorderLayout(0, 3));

        panelStop.setBorder(javax.swing.BorderFactory.createTitledBorder("Stop"));
        togButStopAtNextGen.setText("Stop at next gen");
        togButStopAtNextGen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                togButStopAtNextGenActionPerformed(evt);
            }
        });

        panelStop.add(togButStopAtNextGen, java.awt.BorderLayout.NORTH);

        togStopSaveQuit.setText("Stop in " + config.nDaysToRun + " days at 8:15");
        togStopSaveQuit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                togStopSaveQuitActionPerformed(evt);
            }
        });

        panelStop.add(togStopSaveQuit, java.awt.BorderLayout.SOUTH);

        panelTop.add(panelStop);

        getContentPane().add(panelTop, java.awt.BorderLayout.NORTH);

        panelStats.setLayout(new java.awt.BorderLayout());

        panelStats.setBorder(javax.swing.BorderFactory.createTitledBorder("Statistics"));
        panelBestSoFar.setLayout(new java.awt.BorderLayout(5, 0));

        jLabel3.setText("Best tree so far:");
        panelBestSoFar.add(jLabel3, java.awt.BorderLayout.WEST);

        textBestSoFar.setEditable(false);
        textBestSoFar.setText("Best So Far");
        panelBestSoFar.add(textBestSoFar, java.awt.BorderLayout.CENTER);

        butCopy.setText("Copy to clipboard");
        butCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butCopyActionPerformed(evt);
            }
        });

        panelBestSoFar.add(butCopy, java.awt.BorderLayout.EAST);

        panelStats.add(panelBestSoFar, java.awt.BorderLayout.SOUTH);

        panelCentralStats.setLayout(new java.awt.BorderLayout());

        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 20));

        panelStatsNames.setLayout(new javax.swing.BoxLayout(panelStatsNames, javax.swing.BoxLayout.Y_AXIS));

        labelGenName.setText("Generation:");
        panelStatsNames.add(labelGenName);

        labelBestFitThisName.setText("Best fitness:");
        panelStatsNames.add(labelBestFitThisName);

        labelAvgFitName.setText("Average fitness: ");
        panelStatsNames.add(labelAvgFitName);

        labelAvgNodesName.setText("Average nodes:");
        panelStatsNames.add(labelAvgNodesName);
        labelAvgNodesName.getAccessibleContext().setAccessibleName("Avg nodes this gen: ");

        labelFake3.setText(" ");
        panelStatsNames.add(labelFake3);

        labelBestSoFar.setText("Best so far");
        panelStatsNames.add(labelBestSoFar);

        labelBestSoFarFitName.setText("Fitness:");
        panelStatsNames.add(labelBestSoFarFitName);

        labelBestSoFarHR0Name.setText("HR0:");
        panelStatsNames.add(labelBestSoFarHR0Name);

        labelBestSoFarHR1Name.setText("HR1:");
        panelStatsNames.add(labelBestSoFarHR1Name);

        labelBestSoFarNodesName.setText("Nodes:");
        panelStatsNames.add(labelBestSoFarNodesName);

        jPanel2.add(panelStatsNames);

        panelStatsValues.setLayout(new javax.swing.BoxLayout(panelStatsValues, javax.swing.BoxLayout.Y_AXIS));

        panelStatsValues.setAlignmentX(0.0F);
        labelGenValue.setText(" ");
        panelStatsValues.add(labelGenValue);

        labelBestFitThisValue.setText(" ");
        panelStatsValues.add(labelBestFitThisValue);

        labelAvgFitValue.setText(" ");
        panelStatsValues.add(labelAvgFitValue);

        labelAvgNodesValue.setText(" ");
        panelStatsValues.add(labelAvgNodesValue);

        labelFake4.setText(" ");
        panelStatsValues.add(labelFake4);

        labelFake5.setText(" ");
        panelStatsValues.add(labelFake5);

        labelBestSoFarFitValue.setText(" ");
        panelStatsValues.add(labelBestSoFarFitValue);

        labelBestSoFarHR0Value.setText(" ");
        panelStatsValues.add(labelBestSoFarHR0Value);

        labelBestSoFarHR1Value.setText(" ");
        panelStatsValues.add(labelBestSoFarHR1Value);

        labelBestSoFarNodesValue.setText(" ");
        panelStatsValues.add(labelBestSoFarNodesValue);

        jPanel2.add(panelStatsValues);

        panelCentralStats.add(jPanel2, java.awt.BorderLayout.WEST);

        panelPlot.setLayout(new java.awt.BorderLayout());

        panelPlot.setPreferredSize(new java.awt.Dimension(450, 200));
        panelCentralStats.add(panelPlot, java.awt.BorderLayout.EAST);

        panelStats.add(panelCentralStats, java.awt.BorderLayout.CENTER);

        getContentPane().add(panelStats, java.awt.BorderLayout.CENTER);

        panelBottom.setLayout(new java.awt.BorderLayout());

        panelBottom.setBorder(javax.swing.BorderFactory.createTitledBorder("Status"));
        labInfo.setText("Stopped");
        panelBottom.add(labInfo, java.awt.BorderLayout.CENTER);

        labelProgress.setText("Total progress");
        jPanel1.add(labelProgress);

        jPanel1.add(progressBar);

        panelBottom.add(jPanel1, java.awt.BorderLayout.EAST);

        getContentPane().add(panelBottom, java.awt.BorderLayout.SOUTH);

        getContentPane().add(jPanel3, java.awt.BorderLayout.EAST);

        pack();
    }
    // </editor-fold>//GEN-END:initComponents

    private void togStopSaveQuitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_togStopSaveQuitActionPerformed
        if (togStopSaveQuit.isSelected())
        {
            timer = new Timer();
            Calendar tomorrow = Calendar.getInstance();            
            //Set for config.nDaysToRun at 8:15:00 AM
            //TODO: This should be customizable
            tomorrow.add(Calendar.DATE, config.nDaysToRun);
            tomorrow.set(Calendar.HOUR_OF_DAY, 8);
            tomorrow.set(Calendar.MINUTE, 15);
            tomorrow.set(Calendar.SECOND, 0);
            Logger.log("Setting to stop, save and quit at " + tomorrow.getTime());
            StopTimer stopTimer = new StopTimer(this);
            timer.schedule(stopTimer, tomorrow.getTime());
        }
        else
        {
            Logger.log("Stop timer cancelled");
            stopSaveQuit = false;
            timer.cancel();
            timer.purge();
        }
        
    }//GEN-LAST:event_togStopSaveQuitActionPerformed

    private void butCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butCopyActionPerformed
        StringSelection bestString = new StringSelection(textBestSoFar.getText());
        java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(bestString, bestString);
    }//GEN-LAST:event_butCopyActionPerformed

    private void butSaveNowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSaveNowActionPerformed
        save();
    }//GEN-LAST:event_butSaveNowActionPerformed

    private void togButStopAtNextGenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_togButStopAtNextGenActionPerformed
        stopAtNextGen = !stopAtNextGen;
    }//GEN-LAST:event_togButStopAtNextGenActionPerformed

    private void butGoNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butGoNActionPerformed
        go((Integer)spinGoN.getValue());
    }//GEN-LAST:event_butGoNActionPerformed

    private void butGo1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butGo1ActionPerformed
        go(1);
    }//GEN-LAST:event_butGo1ActionPerformed

    private void butNewFromFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butNewFromFileActionPerformed
        newEvo(true);
    }//GEN-LAST:event_butNewFromFileActionPerformed

    private void butNewFromScratchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butNewFromScratchActionPerformed
        newEvo(false);
    }//GEN-LAST:event_butNewFromScratchActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GPaltaGUI().setVisible(true);
            }
        });
    }
    
    private void plotInit()
    {
        if (plot!=null)
        {
            panelPlot.remove(plot);
        }
        plot = new Plot();
        
        //plot.setSize(200, 10);
        plot.setYRange(0, 1);
        plot.setXRange(0, 50);
        plot.setXLabel("Generation");
        //plot.setYLabel("Fitness");
        if (config.fitness.equals("classifier"))
        {
            plot.setTitle("Hit Rate");
            plot.addLegend(1, "HR0");
            plot.addLegend(2, "HR1");
        }
        else
        {
            plot.setTitle("Fitness");
        }
        //Fake points to set ranges correctly when loading from evo file
        plot.addPoint(3, 0, 0, false);
        plot.addPoint(3, 0, 1, false);
        panelPlot.add(plot);
    }
    
    private void newEvo(boolean fromFile)
    {
        if (usePlot)
        {
            plotInit();
        }
        Logger.log("Starting GPalta, " + (Calendar.getInstance()).getTime()  );
        if (fromFile) 
        {
            Logger.log("Loading evolution from file...");
        }
        else
        {
            Logger.log("Generating evolution...");
        }
        
        //Exceptions caught inside EvolutionThread:
        
        /* Tell the old thread to terminate, or it will
         * remain in wait state and stay in memory forever!!!
         */
        if (evoThread != null)
        {
            evoThread.exit();
        }
        evoThread = new EvolutionThread(this,  fromFile);
        evoThread.start();

        //TODO:  log more information (fitness parameters, etc)
        if (config.rememberLastEval)
            Logger.log("WARNING: \t rememberLastEval should not be used if test" +
                    "\n\t\t cases change their values between generations");
        Logger.log("Initial population info:");
        Logger.log("\t Population Size:     " + config.populationSize);
        Logger.log("\t Maximun Depth:       " + config.maxDepth);
        
        Logger.log("Selection Method:     " + config.selectionMethod);
        if (!config.selectionMethod.equals("tournament"))
        {
            Logger.log("Ranking Type :     " + config.rankingType);
            
        }
        
        
        setEnabledAll(disableAtStart, true);
        setEnabledAll(disableWhenNotRunning, false);
        progressBar.setStringPainted(false);
        progressBar.setValue(0);
        first = true;
        if (fromFile)
        {
            first = false;
        }
    }
    
    private void go(int n)
    {
        Logger.log("Starting evolution for " + n + " generations");
        setEnabledAll(disableWhenRunning, false);
        setEnabledAll(disableWhenNotRunning, true);
        progressBar.setMaximum(n);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setString("0/" + n);
        nGenDone = 0;
        //This need to go at last in case the evoThread is too quick
        evoThread.go(n);
    }
    
    private void save()
    {
        try
        {
            evoThread.save();
        }
        catch (IOException e)
        {
            //TODO: error when saving evolution to file. Do something
            Logger.log(e);
        }
    }
    
    public void notifyReady()
    {
        setEnabledAll(disableWhenRunning, true);
        setEnabledAll(disableWhenNotRunning, false);
        labInfo.setText("Stopped");
        togButStopAtNextGen.setSelected(false);
        stopAtNextGen = false;
        if (stopSaveQuit)
        {
            save();
            try
            {
                Runtime.getRuntime().exec("shutdown -l");
            }
            catch (IOException e)
            {
                Logger.log("Closing session was not possible");
                Logger.log(e);
            }
            finally
            {
                //Exit anyway
                System.exit(0);
            }
        }
    }
    
    public void reportStatus(String status)
    {
        labInfo.setText(status);
    }
    
    public void updateStats(EvolutionStats evoStats)
    {
        if (evoStats.bestTreeChanged)
        {
            textBestSoFar.setText("" + evoStats.bestSoFar);
            labelBestSoFarFitValue.setText("   " + String.format("%.3f",evoStats.bestSoFar.fitness));
            labelBestSoFarHR0Value.setText("   " + String.format("%.3f",evoStats.bestSoFar.hr0));
            labelBestSoFarHR1Value.setText("   " + String.format("%.3f",evoStats.bestSoFar.hr1));
            labelBestSoFarNodesValue.setText("   " + evoStats.bestSoFar.nSubNodes);
        }
        if (usePlot)
        {
            if (config.fitness.equals("classifier"))
            {
                plot.addPoint(1, (double)evoStats.generation, evoStats.bestSoFar.hr0, !first);
                plot.addPoint(2, (double)evoStats.generation, evoStats.bestSoFar.hr1, !first);
            }
            else
            {
                plot.addPoint(1, (double)evoStats.generation, evoStats.bestSoFar.fitness, !first);
            }
            if (evoStats.generation > plot.getXRange()[1])
            {
                plot.addPoint(3, plot.getXRange()[1] +50, 1, false);
                plot.fillPlot();
            }
            //plot.clear(true);
        }
        
        /* The first update is for evaluation of initial population (gen 0), so we
         * haven't really advanced a generation
         */        
        if (first)
        {
            first = false;
        }
        else
        {
            nGenDone++;
        }
        progressBar.setValue(nGenDone);
        progressBar.setString(nGenDone + "/" + progressBar.getMaximum());
        labelAvgFitValue.setText("   " + String.format("%.3f",evoStats.avgFit));
        labelAvgNodesValue.setText("   " + String.format("%.1f",evoStats.avgNodes));
        labelBestFitThisValue.setText("   " + String.format("%.3f",evoStats.bestFitThisGen));
        labelGenValue.setText("   " + evoStats.generation);
        
        if (evoStats.bestSoFar.fitness > config.stopFitness)
        {
            stopAtNextGen = true;
            Logger.log("Fitness reached " + config.stopFitness + " at generation " + evoStats.generation);
        }
    }
    
    public boolean saveEnabled()
    {
        return checkSave.isSelected();
    }
    
    public int getSaveInterval()
    {
        return (Integer)spinSaveInterval.getValue();
    }
    
    private void setEnabledAll(List<javax.swing.JComponent> l, boolean enabled)
    {
        for (javax.swing.JComponent c : l)
            c.setEnabled(enabled);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butCopy;
    private javax.swing.JButton butGo1;
    private javax.swing.JButton butGoN;
    private javax.swing.JButton butNewFromFile;
    private javax.swing.JButton butNewFromScratch;
    private javax.swing.JButton butSaveNow;
    private javax.swing.JCheckBox checkSave;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollBar jScrollBar1;
    private javax.swing.JLabel labInfo;
    private javax.swing.JLabel labelAvgFitName;
    private javax.swing.JLabel labelAvgFitValue;
    private javax.swing.JLabel labelAvgNodesName;
    private javax.swing.JLabel labelAvgNodesValue;
    private javax.swing.JLabel labelBestFitThisName;
    private javax.swing.JLabel labelBestFitThisValue;
    private javax.swing.JLabel labelBestSoFar;
    private javax.swing.JLabel labelBestSoFarFitName;
    private javax.swing.JLabel labelBestSoFarFitValue;
    private javax.swing.JLabel labelBestSoFarHR0Name;
    private javax.swing.JLabel labelBestSoFarHR0Value;
    private javax.swing.JLabel labelBestSoFarHR1Name;
    private javax.swing.JLabel labelBestSoFarHR1Value;
    private javax.swing.JLabel labelBestSoFarNodesName;
    private javax.swing.JLabel labelBestSoFarNodesValue;
    private javax.swing.JLabel labelFake3;
    private javax.swing.JLabel labelFake4;
    private javax.swing.JLabel labelFake5;
    private javax.swing.JLabel labelGen;
    private javax.swing.JLabel labelGenName;
    private javax.swing.JLabel labelGenValue;
    private javax.swing.JLabel labelProgress;
    private javax.swing.JPanel panelBestSoFar;
    private javax.swing.JPanel panelBottom;
    private javax.swing.JPanel panelCentralStats;
    private javax.swing.JPanel panelEvolution;
    private javax.swing.JPanel panelGo;
    private javax.swing.JPanel panelPlot;
    private javax.swing.JPanel panelSaving;
    private javax.swing.JPanel panelStats;
    private javax.swing.JPanel panelStatsNames;
    private javax.swing.JPanel panelStatsValues;
    private javax.swing.JPanel panelStop;
    private javax.swing.JPanel panelTop;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JSpinner spinGoN;
    private javax.swing.JSpinner spinSaveInterval;
    private javax.swing.JTextArea textBestSoFar;
    private javax.swing.JToggleButton togButStopAtNextGen;
    private javax.swing.JToggleButton togStopSaveQuit;
    // End of variables declaration//GEN-END:variables
    
}
